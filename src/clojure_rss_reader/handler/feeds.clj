(ns clojure-rss-reader.handler.feeds
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))


(defmethod ig/init-key ::get [_ {{:keys [spec]} :db}]
  (fn [_]
    (let [result (jdbc/execute! spec ["SELECT * FROM feed"] {:builder-fn rs/as-unqualified-lower-maps})]
      (println result)
      [::response/ok {:message "OK"}])))

(defmethod ig/init-key ::post [_ _]
  (fn [_]
    [::response/ok {:message "OK"}]))