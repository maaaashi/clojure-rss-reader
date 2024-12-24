(ns clojure-rss-reader.handler.feeds
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [honey.sql :as sql]
            [clj-http.client :as http]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as zx]))

(def select-feeds (sql/format {:select [:*]
                               :from [:feed]}))

(defn select-articles [{:keys [id]}]
  (sql/format {:select [:*]
               :from [:article]
               :where [:= :feed_id id]}))

(defn get-feeds [db]
  (let [feeds (jdbc/execute! db select-feeds {:builder-fn rs/as-unqualified-lower-maps})
        feeds-articles (map #(->> (jdbc/execute! db (select-articles %) {:builder-fn rs/as-unqualified-lower-maps})
                                  (assoc % :articles)) feeds)]
    {:feeds feeds-articles}))

(defmethod ig/init-key ::get [_ {{:keys [spec]} :db}]
  (fn [_]
    [::response/ok (get-feeds spec)]))


(defn fetch-rss [url]
  (:body (http/get url {:as :text})))

(defn zipper [rss-content]
  (-> rss-content
      (xml/parse-str)
      (zip/xml-zip)))

(defn extract-articles [entry]
  (let [title (zx/xml1-> entry :title zx/text)
        url (zx/xml1-> entry :url zx/text)
        content (zx/xml1-> entry :content zx/text)
        author (zx/xml1-> entry :author zx/text)
        publicshed (zx/xml1-> entry :published zx/text)]
    {:title title
     :url url
     :content content
     :author author
     :published publicshed}))

(defn extract-feed [zipper]
  (let [title (zx/xml1-> zipper :title zx/text)
        description (zx/xml1-> zipper :description zx/text)
        entries (zx/xml-> zipper :entry)
        articles (map extract-articles entries)]
    {:title title :description description :articles articles}))

(defn insert-article [{:keys [feed_id title url content author published]}]
  (sql/format {:insert-into
               :article
               :columns [:feed_id :title :url :content :author :published_at]
               :values [{:feed_id feed_id :title title :url url :content content :author author :published_at published}]}))

(defn register-articles [db {:keys [feed_id articles]}]
  (doall (map #(jdbc/execute! db (insert-article (assoc % :feed_id feed_id))) articles)))

(defn insert-feed [{:keys [url title description]}]
  (sql/format {:insert-into
               :feed
               :columns [:url :title :description]
               :values [{:url url :title title :description description}]
               :returning [:id]}))

(defn register-feed [db feed-data]
  (let [feed-ids (jdbc/execute! db (insert-feed feed-data) {:builder-fn rs/as-unqualified-lower-maps})
        id (-> feed-ids first :id)]
    (register-articles db (assoc feed-data :feed_id id))))

(defn post-feeds [db {{:keys [url]} :body-params}]
  (let [c (fetch-rss url)
        z (zipper c)
        f (extract-feed z)
        data (assoc f :url url)]
    (register-feed db data))
  [::response/ok {:message "OK"}])

(defmethod ig/init-key ::post [_ {{:keys [spec]} :db}]
  (partial post-feeds spec))
