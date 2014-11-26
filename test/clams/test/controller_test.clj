(ns clams.test.controller-test
  (:require [clojure.test :refer :all]
            [clams.controller :refer :all]
            [clams.params :as p]))

(defcontroller foobar
   "My super good controller function."
   [id :- p/Str amount :- p/Int]
   [id (inc amount)])

(deftest defcontroller-test
  (let [metadata (meta #'foobar)]
    (is (= (:doc metadata) "My super good controller function."))
    (is (= (:params metadata) [:id p/Str :amount p/Int]))
    (is (= (foobar "ID-100" 998) ["ID-100" 999]))))



(defcontroller basic "doc"   [id :- p/Str amount :- p/Int] (str id " " amount))
(defcontroller default-type  [id amount :- p/Int]          (str id " " amount))
(defcontroller default-types [id amount]                   (str id " " amount))
(defcontroller any-type      [id :- p/Any amount]          (str id " " amount))
(defcontroller no-args       []                            (str "empty"))

(defmacro params [name] `(:params (meta (var ~name))))

(deftest arg-check-test
  (is (= [:id p/Str :amount p/Int] (params basic)))
  (is (= [:id p/Any :amount p/Int] (params default-type)))
  (is (= [:id p/Any :amount p/Any] (params default-types)))
  (is (= [:id p/Any :amount p/Any] (params any-type)))
  (is (= [] (params no-args))))

(deftest fail-args-test
  (is (thrown? IllegalArgumentException
               (eval `(defcontroller fail-1
                        "doc"
                        [id p/Str amount :- p/Int]
                        (str id " " amount)))))
  (is (thrown? IllegalArgumentException
               (eval `(defcontroller fail-2
                        [id p/Str amount p/Int]
                        (str id " " amount))))))
