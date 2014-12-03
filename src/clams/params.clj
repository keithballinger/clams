(ns clams.params
  "Parameter validation and binding.

  There are three classes of Clams parameters:

  1. Built-ins. This is the default. These can be annotated with any of the
     schema annotations defined in this namespace (e.g. Str, Int, etc.). These
     parameters are expected to exist in the request map under the `:params` key.

  2. Request. This refers to the request map itself, in its entirety. This is
     your escape hatch whenever you need to do processing on the request and
     Clams hasn't provided a nicer way of handling it. Ideally you won't need
     this very often.

  3. User-defined. Sometimes you may need to access portions of the request map
     that aren't user-passed params, but meanwhile you do so frequently enough
     that having to pass the full request and extract the needed data is
     cumbersome. In these cases, you may define a function which takes the
     request map as its parameter and pass it as your schema annotation. During
     parameter validation, this function will be called and the result will be
     bound to the associated variable.
  "
  (:require [clams.response :as response]
            [clams.util :refer [redef]]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [schema.utils :as utils]))

(redef schema.core [Any Bool Int Keyword Num Str])

(def Request
  "The schema annotation for a Ring request object"
  :request)

(defn- user-defined-type?
  "Tests whether a 'type' arg is user-defined (and therefore arbitrary)."
  [t]
  (not (or (coll? t) (= Request t) (satisfies? s/Schema t))))

(def ^:private schema-key :params)

(defn- param-metadata
  "Extracts the parameter schema from function metadata if one exists.
  Note that you must pass in the var here, not the function itself."
  [fnvar]
  (let [metadata (meta fnvar)]
    (when (contains? metadata schema-key)
      (map vec (partition 2 (schema-key metadata))))))

(defn- metadata->schema
  "Transforms the params metadata format into a schema format
  appropriate for validation. Our internal Request type is not part
  of the schema validation."
  [params]
  (into {}
        (filter (fn [[name type]]
                  (when-not (or (user-defined-type? type) (= type Request))
                    [name type]))
                params)))

(defn- parse-request-params
  "Parses the params in the request, including coercion from JSONish types
  into Clojure types, and validation against the specified schema."
  [req schema]
  (let [parser (coerce/coercer schema coerce/json-coercion-matcher)
        params (parser (:params req))]
    (if (utils/error? params)
      (response/bad-request! (str "Parameter validation failed. Got: " (:params req)))
      params)))

(defn- get-arg [valid-params param-name param-type req]
  (cond
    (user-defined-type? param-type) (param-type req)
    (= param-type Request) req
    :else (get valid-params param-name)))

(defn wrap-controller
  "Wraps a controller function in a function that appropriately parses
  the controller's specified parameters. Note: Because we depend on its
  metadata, the controller function should be passed as a var."
  [ctrl]
  (if-let [param-meta (param-metadata ctrl)]
    (fn [req]
      (let [param-schema  (metadata->schema param-meta)
            valid-params  (parse-request-params req param-schema)
            args          (for [[name type] param-meta]
                            (get-arg valid-params name type req))]
        (apply ctrl args)))
    ctrl))
