(ns clams.app
  (:require [org.httpkit.server :as httpkit]
            [clams.route :refer [compile-routes]]))

(defonce ^:private server (atom nil))

(defn- wrap-middleware
  [routes middleware]
  (reduce #(%2 %1) routes middleware))

(defn- app
  [app-ns middleware]
  (let [routes-ns (symbol (str app-ns ".routes"))]
    (require routes-ns)
    (wrap-middleware
      (compile-routes app-ns (var-get (ns-resolve routes-ns 'routes)))
      middleware)))

(defn start-server
  ([app-ns]
    (start-server app-ns {}))
  ([app-ns opts]
    (when (nil? @server)
      (let [middleware (:middleware opts)
            port       (get opts :port 5000)]
        (reset! server (httpkit/run-server (app app-ns middleware) {:port port}))))))

(defn stop-server
  []
  (let [stop @server]
    (when-not (nil? stop)
      (stop)
      (reset! server nil))))
