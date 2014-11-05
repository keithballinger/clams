(ns clams.app
  (:require [clams.route :refer [compile-routes]]
            [org.httpkit.server :as httpkit]
            ring.middleware.json
            ring.middleware.keyword-params
            ring.middleware.nested-params
            ring.middleware.params))

(defonce ^:private server (atom nil))

(defonce ^:private default-middleware
  [ring.middleware.keyword-params/wrap-keyword-params
   ring.middleware.nested-params/wrap-nested-params
   ring.middleware.params/wrap-params
   #(ring.middleware.json/wrap-json-body % {:keywords? true})
   ring.middleware.json/wrap-json-params
   ring.middleware.json/wrap-json-response])

(defn- wrap-middleware
  [routes middleware]
  (reduce #(%2 %1) routes middleware))

(defn- app
  [app-ns app-middleware]
  (let [routes-ns  (symbol (str app-ns ".routes"))
        middleware (concat default-middleware app-middleware)]
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
