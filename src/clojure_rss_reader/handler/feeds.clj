(ns clojure-rss-reader.handler.feeds
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]))

(defmethod ig/init-key ::get [_ _]
  (fn [_]
    [::response/ok {:message "OK!!!"}]))