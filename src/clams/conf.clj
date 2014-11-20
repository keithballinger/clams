(ns clams.conf
  "Provides app configuration. Clams app configuration is read both from
  EDN-formatted files compiled into resources and environment variables.
  This namespace contains both the infrastructure for loading these values
  and accessing them at run-time.

  Clams provides a sane configuration out-of-the-box in its base config file.
  This should be sufficient for the simplest apps. An app may override these
  settings and add custom values by creating an additional config file at
  `/resources/conf/default.edn`.

  Apps may also add environment-specific config files. These are selectable at
  run-time by setting the special environment variable `CLAMS_ENV`. Their values
  are merged on top of, and take precedence over, the default and base configs.
  For example, to activate a production-specific configuration, one might run
  the command: `CLAMS_ENV=prod lein run`. The value of `CLAMS_ENV` can be an
  arbitrary string; the value of that string determines which config is loaded.

  Environment variables are also merged into the config. These take the highest
  precedence and can override any file's value. Names of environment variables
  are normalized from UPPER_UNDERSCORE_CASE strings to more Clojure-esque
  lower-dash-case keywords.
  "
  (:refer-clojure :rename {get core-get})
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def ^:private conf (atom nil))

(defn- normalize-env-var-name
  "Environment variables are often written in UPPER_UNDERSCORE_CASE.
  Since Clojure keywords are ordinarily written in lower-dash-case,
  this function normalizes them to match."
  [name]
  (if-not (nil? name)
    (-> name
        string/lower-case
        (string/replace "_" "-")
        (string/replace "." "-")
        keyword)))

(defn- getenv
  "Wrapper around System/getenv. Makes it easier to mock in tests."
  []
  (System/getenv))

(defn- read-env
  "Reads the environment and processes it into a normalized map."
  []
  (into {} (for [[k v] (getenv)]
    [(normalize-env-var-name k) v])))

(defn- get-clams-env
  "Parses from a normalized env map the special CLAMS_ENV var. The value
  of this var informs which additional config files should be loaded."
  [env]
  (when-let [cenv (:clams-env env)]
    (string/lower-case cenv)))

(defn- read-config-file
  "Reads from resources the config file of the given name and processes it
  into a normalized map."
  [name]
  (when name
    (let [resource (io/resource (format "conf/%s.edn" name))]
      (if (nil? resource)
        {}  ;; Config file not found.
        (edn/read-string (slurp resource))))))

(defn load!
  "Loads the app config. Usually you won't need to call this directly as
  the config will be automatically loaded when you attempt to access it."
  []
  (let [env  (read-env)
        cenv (get-clams-env env)]
    (reset! conf (merge (read-config-file "base")
                        (read-config-file "default")
                        (read-config-file cenv)
                        env))))

(defn unload!
  "Unloads the app config. This exists mainly to ease testing
  and should very rarely, if ever, be called in your app."
  []
  (reset! conf nil))

(defn loaded?
  "Tests whether the config has been loaded."
  []
  (not (nil? @conf)))

(defn get-all
  "Returns the entire config map."
  []
  (when-not (loaded?)
    (load!))
  @conf)

(defn get
  "Returns the config value for the given key. If a not-found argument is
  passed, that will be returned if no value is found, otherwise nil."
  ([k]
    (get k nil))
  ([k not-found]
    (core-get (get-all) k not-found)))
