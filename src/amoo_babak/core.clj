(ns amoo-babak.core
  (:gen-class)
  (:use org.httpkit.server)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.params :as midpar]
            [clojure.core.async :as async]
            [amoo-babak.amoo-request :refer :all]))


(defn serve [req]
  (def params (:params req))
  (get-lyrics (params "artist") (params "song") nil)
  )

(defroutes app-routes
           (GET "/" request serve)
           (route/not-found "Babak Babak Babak, Babak zade kapak"))

(def app
  (midpar/wrap-params app-routes))

(defn -main
  [& args]
  (run-server #'app {:port 6363})
  )




