(ns clams.controller
  (:require [clojure.tools.macro :as macro]))

(defmacro defcontroller
  "Defines a controller function. You don't really need this macro,
  it just makes things a little prettier."
  [sym & args]
  (let [[sym body]  (macro/name-with-attributes sym args)
        meta-params (vec (flatten (map #(do [(keyword (first %)) (second %)])
                                       (partition 2 (first body)))))
        args        (vec (take-nth 2 (first body)))]
    `(defn ~(vary-meta sym assoc :params meta-params) ~args ~@(rest body))))
