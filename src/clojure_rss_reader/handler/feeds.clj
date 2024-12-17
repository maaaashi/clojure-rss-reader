(ns clojure-rss-reader.handler.feeds
  (:require [integrant.core :as ig]
            [ring.util.http-response :as res]))

(defmethod ig/init-key ::get [_ _]
  (fn [_]
    (res/ok "OK")))

(defmethod ig/init-key ::post [_ _]
  (fn [_]
    (res/ok "OK")))
