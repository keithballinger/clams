(defproject clams
  "0.1.0-SNAPSHOT"
  :description "Clams"
  :url "http://github.com/standardtreasury-internal/clams"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
    [
     [clout "2.0.0"]
     [compojure "1.2.0"]
     [http-kit "2.1.19"]
     [metosin/ring-http-response "0.5.2"]
     [org.clojure/clojure "1.6.0"]
     [org.clojure/tools.macro "0.1.5"]
     [prismatic/schema "0.3.1"]
     [ring "1.3.1"]
     [ring-mock "0.1.5"]
     [ring/ring-json "0.3.1"]
    ]
  :repositories {"internal" {:url "s3://standard-releases/releases/"
                             :username :env/aws_access_key_id
                             :passphrase :env/aws_secret_access_key
                             :sign-releases false}}
  :plugins
    [
     [jonase/eastwood "0.1.5"]
     [lein-test-out "0.3.1"]
     [lein-maven-s3-wagon "0.2.4"]
    ])
