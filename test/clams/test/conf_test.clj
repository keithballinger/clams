(ns clams.test.conf-test
  (:require [clojure.test :refer :all]
            [clams.conf :as conf]))

(def mock-config-edn
  "{
     :database-url \"sql://fake:1234/foobar\"
     :log-level :debug ;; verbose!
   }")

(defn wrap-edn-fixtures
  [f]
  (fn []
    (with-redefs [clojure.java.io/resource (fn [_] "MOCK")
                  clojure.core/slurp       (fn [_] mock-config-edn)]
      (f))))

(defn wrap-env-fixtures
  [f]
  (fn []
    (with-redefs [clams.conf/getenv (fn [] {"DATABASE_URL" "sql://dev.fake:1234/foobar"})]
      (f))))

(use-fixtures :each (fn [f]
  (f)
  (conf/unload!)))

(deftest not-loaded-test
  (is (thrown-with-msg? AssertionError #"not loaded" (conf/get :log-level))))

(deftest get-from-edn-test
  ((wrap-edn-fixtures conf/load!))
  (is (= (conf/get :database-url) "sql://fake:1234/foobar"))
  (is (= (conf/get :log-level) :debug)))

(deftest get-from-env-test
  ((-> conf/load! wrap-env-fixtures wrap-edn-fixtures))
  (is (= (conf/get :database-url) "sql://dev.fake:1234/foobar"))
  (is (= (conf/get-all) {:database-url "sql://dev.fake:1234/foobar"
                         :log-level    :debug})))
