(defproject format-data "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc  "0.7.0-alpha1"]
                 [java-jdbc/dsl  "0.1.3"]
                 [org.xerial/sqlite-jdbc  "3.16.1"]
                 [org.slf4j/slf4j-nop "1.7.22"]
                 [com.jolbox/bonecp  "0.8.0.RELEASE"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.7.0"]
                                  ;[lazy-files "0.0.1-SNAPSHOT"]
                                  ]}
             ;; You can add dependencies that apply to `lein midje` below.
             ;; An example would be changing the logging destination for test runs.
             :midje {}})
             ;; Note that Midje itself is in the `dev` profile to support
             ;; running autotest in the repl.

  
