(ns clams.test.controller-test
  (:require [clojure.test :refer :all]
            [clams.controller :refer :all]
            [clams.params :as p]))

(defcontroller foobar
  "My super good controller function."
  [id p/Str amount p/Int]
  [id (inc amount)])

(deftest defcontroller-test
  (let [metadata (meta #'foobar)]
    (is (= (:doc metadata) "My super good controller function."))
    (is (= (:params metadata) [:id p/Str :amount p/Int]))
    (is (= (foobar "ID-100" 998) ["ID-100" 999]))))
