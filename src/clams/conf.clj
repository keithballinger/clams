(ns clams.conf
  (:refer-clojure :rename {get core-get})
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn- normalize-key
  [key]
  (if-not (nil? key)
    (-> key
        string/lower-case
        (string/replace "_" "-")
        (string/replace "." "-")
        keyword)))

(defn- read-config
  [name]
  (when name
    (let [resource (io/resource (format "conf/%s.edn" name))]
      (if (nil? resource)
        {}  ;; Config file not found.
        (edn/read-string (slurp resource))))))

(defn- getenv
  []
  (System/getenv))

(defn- read-env
  []
  (into {} (for [[k v] (getenv)]
    [(normalize-key k) v])))

(defn- get-clams-env
  [env]
  (when-let [cenv (:clams-env env)]
    (string/lower-case cenv)))

(defonce ^:private full-conf (atom nil))

(defn load!
  []
  (let [env  (read-env)
        cenv (get-clams-env env)]
    (reset! full-conf (merge (read-config "base")
                             (read-config "default")
                             (read-config cenv)
                             env))))

(defn unload!
  []
  (reset! full-conf nil))

(defn assert-loaded
  []
  (assert (not (nil? @full-conf)) "Config not loaded!"))

(defn get
  ([k]
    (get k nil))
  ([k not-found]
    (assert-loaded)
    (core-get @full-conf k not-found)))

(defn get-all
  []
  (assert-loaded)
  @full-conf)
