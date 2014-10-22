(ns clams.app
  (:require [org.httpkit.server :as httpkit]))

(defonce server (atom nil))

(defn- app
  "TODO: Get rid of this."
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn run-server
  [& args]
  (reset! server (httpkit/run-server app {:port 5000}))
  :ok)
