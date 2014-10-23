(ns clams.app
  (:require [org.httpkit.server :as httpkit]))

(defonce ^:private server (atom nil))

(defn- app
  "TODO: Get rid of this."
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn start-server
  [& args]
  (if (nil? @server)
    (do (reset! server (httpkit/run-server app {:port 5000}))
        nil)))

(defn stop-server
  []
  (let [stop @server]
    (if-not (nil? stop)
      (do (stop)
          (reset! server nil)))))
