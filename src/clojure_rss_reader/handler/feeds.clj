(ns clojure-rss-reader.handler.feeds
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn get-feeds [db]
  (let [result (jdbc/execute! db ["SELECT * FROM feed"] {:builder-fn rs/as-unqualified-lower-maps})]
    {:feeds result}))

(defmethod ig/init-key ::get [_ {{:keys [spec]} :db}]
  (fn [_]
    [::response/ok (get-feeds spec)]))

(defmethod ig/init-key ::post [_ _]
  (fn [_]
    [::response/ok {:message "OK"}]))