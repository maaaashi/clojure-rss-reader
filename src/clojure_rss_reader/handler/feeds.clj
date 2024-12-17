(ns clojure-rss-reader.handler.feeds
  (:require [integrant.core :as ig]))

(defmethod ig/init-key ::get [_ _]
  (fn [_]
    {:status 200 :body "OK"}))

(defmethod ig/init-key ::post [_ _]
  (fn [_]
    {:status 200 :body "OK"}))
