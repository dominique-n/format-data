(ns format-data.db-test
  (require [midje.sweet :refer :all]
           [format-data.db :refer :all]
           [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [java-jdbc.ddl :as ddl]
           [java-jdbc.sql :as sql]))

(against-background 
  [(before :contents
          (def db-path "dev-resources/test.db"))
  (after :contents 
         (io/delete-file db-path true))]

  (facts "About `into-hashmap"
         (into-hashmap [:a :b] [1 2]) => {:a 1, :b 2})

  (facts "About `make-headers"
         (drop 1 (make-headers [:id :title] [:long :string])) => [[:id :integer] [:title :text]]
         )

  (let [pooled-db (pool-db (make-db-spec db-path))
        db-table :test_table
        colnames [:id :title :price :date]
        types [:long :string :double :string]
        rows [[1 "dog" 123.50 "2008-05-23"]
              [1 "fish" 12 "2008-06-21"]]]
    (facts "About `to-sql"
         (do (to-sql db-path db-table colnames types rows)
             (-> (jdbc/query pooled-db "select count(1) from test_table") first vals first)) => 2))
  )


