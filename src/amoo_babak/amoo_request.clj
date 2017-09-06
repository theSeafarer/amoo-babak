(ns amoo-babak.amoo-request
  (:require [org.httpkit.client :as http]
            [hickory.select :as s]
            [clojure.string :as string]
            [cheshire.core :refer :all]
            [datascript.core :as d])
  (:use hickory.core)
  (:import (java.net URLEncoder)))

(def get-url "http://lyrics.wikia.com/wiki/")
(def search-url "http://lyrics.wikia.com/index.php?action=ajax&rs=getLinkSuggest&format=json&query=")
(def conn (d/create-conn))

(defn url-encode     [s]     (URLEncoder/encode (str s) "utf8"))

(defn get-db [artist song]
  (let [from-db (vec (remove #{}
    (d/q '[:find ?l
             :in $ [?a ?s]
             :where
              [?e :artist ?a]
              [?e :song ?s]
              [?e :lyrics ?l] ]
              @conn
              [artist song])))]
              (if (empty? from-db)
                nil
                ((from-db 0) 0))))
;seriously? there has to be
;a better way to get a keyless item out of a set

(defn get-wikia [artist song name]
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
  (string/join (map #(string/replace %1 #"\{([^{]*)\}" "\n") lyrics-vec)))


(defn search-lyrics [song]
  (println "searching for lyrics" song)
  (def jsonMap (-> @(http/get (str search-url
                                   (url-encode song))
                              {:as :auto}) :body parse-string))
  (println jsonMap)
  (if (empty? (jsonMap "redirects"))
    "NOT FOUND"
    (get-wikia nil nil ((string/split ((jsonMap "suggestions") 0) #" \(") 0)))
  )

  (defn get-lyrics [artist song]
    (if-let [db-res (get-db artist song)]
      db-res
      (let [result (get-wikia artist song nil)]
        (if (string/blank? result)
          (search-lyrics song)
          (let [final-result ((string/split result #"\{") 0)]
            (println (d/transact! conn [{:db/id -1
                              :artist artist
                              :song song
                              :lyrics final-result}]))
            final-result)))))
