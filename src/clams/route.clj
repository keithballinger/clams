(ns clams.route
  (:require [clojure.string :as string]
            [clams.params :as params]
            [clams.util :refer [redefmacro]]
            clout.core
            compojure.core
            compojure.route))

;; HTTP Methods
(def GET     :get)
(def POST    :post)
(def PUT     :put)
(def DELETE  :delete)
(def HEAD    :head)
(def OPTIONS :options)
(def PATCH   :patch)

;; Special Routes
(def static :static)

(defn controller
  "Returns the namespace and name of the controller function indicated by the
  given app namespace and route key."
  [app-ns route-key]
  (let [segments (map string/lower-case (string/split (name route-key) #"-"))
        ctrl-ns  (symbol (string/join "." (concat [app-ns "controllers"] (drop-last 1 segments))))
        ctrl-fn  (symbol (last segments))]
    [ctrl-ns ctrl-fn]))

(defn resolve-controller
  "Returns the controller function indicated by the given app namespace
  and route key."
  [app-ns route-key]
  (let [[ctrl-ns ctrl-fn] (controller app-ns route-key)]
    (require ctrl-ns)
    (ns-resolve ctrl-ns ctrl-fn)))

(defn- make-controller
  [app-ns route-key]
  (params/wrap-controller (resolve-controller app-ns route-key)))

;; Hack around Compojure's private and macro-oriented prepare-route function.
;; This is less than ideal.
(defn- prepare-route
  [route]
  (cond
    (string? route)
      (clout.core/route-compile route)
    (vector? route)
      (clout.core/route-compile (first route) (apply hash-map (rest route)))
    :else
      (throw (Exception. "Unusual route; not sure what to do; dying."))))

(defn- make-app-routes
  [app-ns routes]
  (for [[method pathspec route-key opts] routes]  ;; TODO: opts currently is useless
    (cond
      (= method static)
        (compojure.route/resources pathspec {:root "static"})
      :else
        (compojure.core/make-route
          method
          (prepare-route pathspec)
          (make-controller app-ns route-key)))))

(defn- make-default-routes
  []
  [(compojure.route/not-found "404 Not Found")])

(defn compile-routes
  [app-ns routes]
  (apply compojure.core/routes
         (concat (make-app-routes app-ns routes) (make-default-routes))))
