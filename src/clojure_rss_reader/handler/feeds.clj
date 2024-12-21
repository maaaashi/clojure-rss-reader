(ns clojure-rss-reader.handler.feeds
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [honey.sql :as sql]
            [clj-http.client :as http]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]))

(def select-all (sql/format {:select [:*] :from [:feed]}))

(defn get-feeds [db]
  (let [result (jdbc/execute! db select-all {:builder-fn rs/as-unqualified-lower-maps})]
    {:feeds result}))

(defmethod ig/init-key ::get [_ {{:keys [spec]} :db}]
  (fn [_]
    [::response/ok (get-feeds spec)]))


(defn fetch-rss [url]
  (:body (http/get url {:as :text})))

(defn parse-rss [rss-content]
  (-> rss-content
      (xml/parse-str)
      (zip/xml-zip)
      (first)))

(defn extract-content-by-tag [item tag]
  (-> (filter #(= (:tag %) tag) item)
      (first)
      (:content)
      (first)))

(defn extract-feed-item [item]
  (let [title (extract-content-by-tag item :title)
        url (extract-content-by-tag item :url)
        content (extract-content-by-tag item :content)
        author (-> (extract-content-by-tag item :author)
                   (first)
                   (:content)
                   (first))
        publicshed (extract-content-by-tag item :published)]
    {:title title
     :url url
     :content content
     :author author
     :published publicshed}))

(defn extract-feed [feed]
  (let [title (extract-content-by-tag feed :title)
        description (extract-content-by-tag feed :description)
        entries (->> (filter #(= (:tag %) :entry) feed)
                    (map #(:content %)))
        items (map extract-feed-item entries)]
    {:title title :description description :items items}))

(defn insert-feed-data [db feed-data]
  ; tood
  )

(defn post-feeds [db {{:keys [url]} :body-params}]
  (let [content (fetch-rss url)
        feed (parse-rss content)
        feed-data (extract-feed (get feed :content))]
    (insert-feed-data db feed-data))
  [::response/ok {:message "OK"}])

(defmethod ig/init-key ::post [_ {{:keys [spec]} :db}]
  (partial post-feeds spec))
