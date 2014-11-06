(ns clams.response
  (:require [clams.util :refer [redef]]
            ring.util.http-response))

;; HTTP Standard Responses
;; http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
(redef ring.util.http-response [
  ;; 100s
  continue
  switching-protocols
  processing

  ;; 200s
  ok
  created
  accepted
  non-authoritative-information
  no-content
  reset-content
  partial-content
  multi-status
  already-reported
  im-used

  ;; 300s
  multiple-choices
  moved-permanently
  found
  see-other
  not-modified
  use-proxy
  temporary-redirect
  permanent-redirect

  ;; 400s
  bad-request
  unauthorized
  payment-required
  forbidden
  not-found
  method-not-allowed
  not-acceptable
  proxy-authentication-required
  request-timeout
  conflict
  gone
  length-required
  precondition-failed
  request-entity-too-large
  request-uri-too-long
  unsupported-media-type
  requested-range-not-satisfiable
  expectation-failed
  enhance-your-calm
  unprocessable-entity
  locked
  failed-dependency
  unordered-collection
  upgrade-required
  precondition-required
  too-many-requests
  request-header-fields-too-large
  retry-with
  blocked-by-parental-controls
  unavailable-for-legal-reasons

  ;; 500s
  internal-server-error
  not-implemented
  bad-gateway
  service-unavailable
  gateway-timeout
  http-version-not-supported
  variant-also-negotiates
  insufficient-storage
  loop-detected
  bandwidth-limit-exceeded
  not-extended
  network-authentication-required
  network-read-timeout
  network-connect-timeout
])
