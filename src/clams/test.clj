(ns clams.test
  (:require clams.app
            [clams.util :refer :all]
            ring.middleware.json
            ring.middleware.keyword-params
            ring.middleware.nested-params
            ring.middleware.params
            ring.mock.request))

(redef ring.mock.request [content-type request])

(defn app
  [app-ns]
  (-> (clams.app/routes app-ns)
      ring.middleware.keyword-params/wrap-keyword-params
      ring.middleware.nested-params/wrap-nested-params
      ring.middleware.params/wrap-params
      (ring.middleware.json/wrap-json-body {:keywords? true})
      ring.middleware.json/wrap-json-params))
