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

(defn extract-feed [zip]
  (let [content (:content zip)]
    content))

(defn extract-feeds [zip]
  (let [content (:content zip)]
    (map extract-feed content))
  )

(comment
  (let [content (fetch-rss "https://qiita.com/tags/clojure/feed")
        zip (parse-rss content)
        feeds (extract-feeds zip) ]
    feeds))

(defn post-feeds [db {{:keys [url]} :body-params}]
  (println url)
  (println (jdbc/execute! db select-all {:builder-fn rs/as-unqualified-lower-maps}))
  [::response/ok {:message "OK"}])

(defmethod ig/init-key ::post [_ {{:keys [spec]} :db}]
  (partial post-feeds spec))
