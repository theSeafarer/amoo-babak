(ns amoo-babak.amoo-request
  (:require [org.httpkit.client :as http]
            [hickory.select :as s]
            [clojure.string :as string]
            [cheshire.core :refer :all])
  (:use hickory.core)
  (:import (java.net URLEncoder)))

(def get-url "http://lyrics.wikia.com/wiki/")
(def search-url "http://lyrics.wikia.com/index.php?action=ajax&rs=getLinkSuggest&format=json&query=")
(declare search-lyrics)

(defn url-encode     [s]     (URLEncoder/encode (str s) "utf8"))

(defn get-lyrics [artist song name]
  (def url (if (nil? name)
             (str get-url
                  (url-encode (string/replace artist #" " "_"))
                  ":"
                  (url-encode (string/replace song #" " "_")))
             (str get-url
                  (url-encode (string/replace name #" " "_")))))
  (println "getting lyrics  " url)

  (def htree (->  @(http/get url
                             {:as :auto}) :body  parse as-hickory))

  (def lyrics-vec (-> (s/select (s/descendant
                                  (s/class "lyricbox"))
                                htree)
                      first :content)
   )

  (map #(string/replace %1 #"\{([^{]*)\}" "\n") lyrics-vec)
  (def result (string/join (map #(string/replace %1 #"\{([^{]*)\}" "\n") lyrics-vec)))

  (if (string/blank? result)
    (search-lyrics song)
     ((string/split result #"\{") 0))

  )


(defn search-lyrics [song]
  (println "searching for lyrics" song)
  (def jsonMap (-> @(http/get (str search-url
                                   (url-encode song))
                              {:as :auto}) :body parse-string))
  (println jsonMap)
  (if (empty? (jsonMap "redirects"))
    "NOT FOUND"
    (get-lyrics nil nil ((string/split ((jsonMap "suggestions") 0) #" \(") 0)))
  )
