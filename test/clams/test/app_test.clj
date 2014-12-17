(ns clams.test.app-test
  (:require [clojure.test :refer :all]
            [clams.app :as app]
            [ring.mock.request :as mock]
            [ring.util.io :refer [string-input-stream]]
            ))

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

(deftest middleware-order-doesnt-unnest-vectors
  ;; We were having a problem where wrap-nested-params was being applied
  ;; after our json middleware, which was flattening vectors by taking
  ;; the first object contained inside and storing that instead. This test
  ;; aims to make sure we don't do that again.
  (let [identity-app ((apply comp (reverse app/default-middleware)) identity)
        sample-nested-json "{\"address\":\"12 Presidio Ave\",\"contacts\":[{\"name\":\"David Jarvis\",\"title\":\"engineer\",\"tax_id\":\"555\"}],\"name\":\"ST\",\"tax_id\":\"5234234\"}"
        response (identity-app {:body (string-input-stream sample-nested-json)
                                :content-type "application/json; charset=UTF-8"})]
    (is (= (:params response)
           {:address "12 Presidio Ave"
            :name "ST"
            :tax_id "5234234"
            :contacts [{:name "David Jarvis"
                        :title "engineer"
                        :tax_id "555"}]}))))
