(ns clams.app
  (:require [org.httpkit.server :as httpkit]
            [clams.route :refer [compile-routes]]))

(defonce ^:private server (atom nil))

(defn- app
  [app-ns]
  (let [routes-ns (symbol (str app-ns ".routes"))]
    (require routes-ns)
    (compile-routes app-ns (var-get (ns-resolve routes-ns 'routes)))))

(defn start-server
  [app-ns & args]
  (if (nil? @server)
    (do (reset! server (httpkit/run-server (app app-ns) {:port 5000}))
        nil)))

(defn stop-server
  []
  (let [stop @server]
    (if-not (nil? stop)
      (do (stop)
          (reset! server nil)))))
