(ns clams.test
  (:require clams.app
            [clams.util :refer :all]
            ring.middleware.json
            ring.middleware.keyword-params
            ring.middleware.nested-params
            ring.middleware.params
            ring.mock.request))

(redef ring.mock.request [content-type body header query-string request])

(defn test-app
  [app-ns & app-middleware]
  (clams.app/app app-ns app-middleware))
