(ns clams.test.app-test
  (:require [clojure.test :refer :all]
            [clams.app :as app]))

(def m1 #(* % 2))
(def m2 #(+ % 7))
(def m3 #(- % 1))

; Hack around privateness of app/wrap-middleware.
(def wrap-middleware #'app/wrap-middleware)

(deftest wrap-middleware-test
  ;; We're using integer math here as a stand-in to test function composition.
  (doseq [[app res]
          [[(wrap-middleware 10 [m1 m2 m3]) 26]
           [(wrap-middleware 10 [m3 m1 m2]) 25]
           [(wrap-middleware 10 [])         10]
           [(wrap-middleware 10 [m2])       17]]]
    (is (= app res))))
