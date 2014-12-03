(ns clams.test.params-test
  (:require [clojure.test :refer :all]
            [clams.params :as p]))

(defn bare-request
  [req]
  (inc (:foobar req)))

(deftest bare-request-test
  (is (= ((p/wrap-controller #'bare-request) {:foobar 1}) 2)))

(defn ^{:params nil} no-meta-1
  []
  1)

(defn ^{:params []} no-meta-2
  []
  2)

(deftest no-meta-test
  (is (= ((p/wrap-controller #'no-meta-1) {:params {}}) 1))
  (is (= ((p/wrap-controller #'no-meta-2) {:params {}}) 2)))

(defn simple-test
  [ctrl successes failures]
  (let [wrapped-ctrl (p/wrap-controller ctrl)]
    (doseq [[foo res] successes]
      (is (= (wrapped-ctrl {:params {:foo foo}}) res)))
    (doseq [foo failures]
      (is (thrown? Exception (wrapped-ctrl {:params {:foo foo}}))))))

(defn ^{:params [:foo p/Bool]} simple-bool
  [foo]
  foo)

(deftest simple-bool-test
  (simple-test #'simple-bool
               [[true true]
                [false false]
                ["true" true]
                ["false" false]
                ["tralse" false]
                ["null" false]]
               [0 1 nil]))

(defn ^{:params [:foo p/Int]} simple-int
  [foo]
  foo)

(deftest simple-int-test
  (simple-test #'simple-int
               [[0 0] [1 1] [-1 -1] [-1.0 -1]]
               [1.1 "1" "-1" "1.00" true nil]))

(defn ^{:params [:req p/Request :foo p/Int]} simple-req-object
  [req foo]
  [(inc (:foo (:params req)))
   (dec foo)])

(deftest simple-req-object-test
  (let [n (rand-int 1000)]
    (simple-test #'simple-req-object
                 [[n [(inc n) (dec n)]]]
                 ["fail" false :foo])))

(defn ^{:params [:foo p/Keyword]} simple-keyword
  [foo]
  foo)

(deftest simple-keyword-test
  (simple-test #'simple-keyword [[:bar :bar] ["bar" :bar]] [0 1 true nil]))

(defn ^{:params [:foo p/Num]} simple-num
  [foo]
  foo)

(deftest simple-num-test
  (simple-test #'simple-num
               [[0 0]
                [1 1]
                [-1 -1]
                [-1.0 -1.0]
                [1.1 1.1]
                [0.0009 0.0009]]
               ["1" "-1" "1.00" true nil]))

(defn ^{:params [:foo p/Str]} simple-str
  [foo]
  foo)

(deftest simple-str-test
  (simple-test #'simple-str
               [["foo" "foo"]
                ["1.0" "1.0"]
                ["null" "null"]
                ["" ""]]
               [1 true nil]))

(defn ^{:params [:foo p/Any]} simple-any
  [foo]
  foo)

(deftest simple-any-test
  (let [obj (Object.)]
    (simple-test #'simple-any
                 [[true true]
                  [88 88]
                  [:bar :bar]
                  [3.14 3.14]
                  ["foo" "foo"]
                  [obj obj]
                  [nil nil]
                  []]
                 [])))

(defn ^{:params [:foo p/Str :bar [p/Num] :baz [{:a p/Int :b p/Int}]]} complex
  [foo bar baz]
  [foo bar baz])

(deftest complex-test
  (let [ctrl (p/wrap-controller #'complex)
        successes [[{:foo ""
                     :bar []
                     :baz []}
                    ["" [] []]]
                   [{:foo "Abc"
                     :bar []
                     :baz nil}
                    ["Abc" [] []]]
                   [{:foo "Abc 123"
                     :bar [1 2.1 3.21]
                     :baz [{:a 1 :b 2}
                           {:a 3 :b 4}]}
                    ["Abc 123" [1 2.1 3.21] [{:a 1 :b 2} {:a 3 :b 4}]]]]
        failures [{:foo "abc"}
                  {:foo nil
                   :bar nil
                   :baz nil}
                  {:foo "Abc"
                   :bar ["123"]
                   :baz [{:a 1 :b 2}]}
                  {:foo "Abc"
                   :bar [1 2 3]
                   :baz [{:a 1}]}
                  {:foo "Abc"
                   :bar [1 2 3]
                   :baz [{:a 1 :b 2.2}]}]]
    (doseq [[params res] successes]
      (is (= (ctrl {:params params}) res)))
    (doseq [params failures]
      (is (thrown? Exception (ctrl {:params params}))))))

(defn arbitrary [req]
  (get-in req [:user :id]))

(defn ^{:params [:foo p/Str :bar arbitrary]} arbitrary-binding
  [foo bar]
  [foo bar])

(deftest arbitrary-binding-test
  (let [ctrl (p/wrap-controller #'arbitrary-binding)]
    (is (= (ctrl {:params {:foo "abc"} :user {:id 123}}) ["abc" 123]))))
