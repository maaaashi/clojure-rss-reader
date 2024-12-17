(ns handler.feeds
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :clojure-rss-reader.handler.feeds/get [_ _]
  (fn [_]
    {:status 200
     :body "OK"}))

(defmethod ig/init-key :clojure-rss-reader.handler.feeds/post [_ _]
  (fn [_]
    {:status 200
     :body "OK"}))
