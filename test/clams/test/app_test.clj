(ns clams.test.app-test
  (:require [clojure.test :refer :all]
            [clams.app :refer :all]))

(deftest vacuous-test
  (testing "It works."
    (is (= 1 1))))
