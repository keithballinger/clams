(ns clams.controller
  (:require
   clams.params
   [clojure.tools.macro :as macro]))

(defn- annotation? [x]
  (= x :-))

(defn- parse-controller-params
  "Given an arg list with annotated optional types, convert to a
   collection of pairs of name => type.

   The args are annotated with types using the :- keyword, with the
   syntax being

      NAME :- TYPE

   Where TYPE is one of the schema types defined in clams.params.  If
   no annotation is given, it is assumed that to be of type
   clams.params/Any.

   For example,

        (parse-controller-params '(a :- clams.schema/Int
                                   b
                                   c :- clams.schema/Str))
        =>
        ([a clams.schema/Int] [b clams.schema/Any] [c clams.schema/Str])
   "
  [args]
  (when (seq args)
    (let [arg (first args)
          xs (next args)]
      (if (= (find-ns 'clams.params) (:ns (meta (resolve arg))))
          (throw (IllegalArgumentException. (format "Invalid parameter schema %s" arg)))
          (if (annotation? (first xs))
              (cons [arg (second xs)]
                    (parse-controller-params (nnext xs)))
              (cons [arg clams.params/Any]
                    (parse-controller-params xs)))))))

(defmacro defcontroller
  "Defines a controller function. You don't really need this macro,
   it just makes things a little prettier.

   The params to defcontroller may take two forms:

   1. An annotated list with the parameter name and type from the
      clams.params package.  We use a `:-` keyword to indicate the
      type:

        (defcontroller mycontroller [myvar :- clams.params/Int] ...)

   2. A regular un-annotated paramater list.  In this case all the
      paramaters are of type `clams.params/Any`."
  [sym & macro-args]
  (let [[sym attrs] (macro/name-with-attributes sym macro-args)
        args        (first attrs)
        body        (rest attrs)
        parsed      (parse-controller-params args)
        params      (vec (mapcat (fn [[sym type]] [(keyword sym) type]) parsed))
        fn-args     (vec (map first parsed))]
    `(defn ~(vary-meta sym assoc :params params) ~fn-args ~@body)))
