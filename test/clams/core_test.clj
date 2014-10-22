(ns clams.core-test
  (:require [clojure.test :refer :all]
            [clams.core :refer :all]))

(deftest vacuous-test
  (testing "It works."
    (is (= 1 1))))
