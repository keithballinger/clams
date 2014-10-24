(ns clams.test.route-test
  (:require [clojure.test :refer :all]
            [clams.route :refer :all]))

(deftest controller-test
  (doseq [[app-ns route-key result]
          [["app"         :foo         ['app.controllers 'foo]]
           ["app"         :foo-bar     ['app.controllers.foo 'bar]]
           ["app"         :foo-bar-baz ['app.controllers.foo.bar 'baz]]
           ["app.sub"     :foo-bar     ['app.sub.controllers.foo 'bar]]
           ["app.sub.sub" :foo-bar     ['app.sub.sub.controllers.foo 'bar]]
           ["app"         :FoO         ['app.controllers 'foo]]
           ["app"         :FOO-BAR     ['app.controllers.foo 'bar]]]]
    (is (= (controller app-ns route-key) result))))
