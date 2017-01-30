(ns format-data.db
  (require [clojure.java.jdbc :as jdbc]
           [java-jdbc.ddl :as ddl]
           [java-jdbc.sql :as sql])
  )


(defn make-db-spec [db_path]
  {:classname   "org.sqlite.JDBC"
   :subprotocol  "sqlite"
   :subname     db_path
   :init-pool-size 4
   :max-pool-size 20
   :partitions 2})

(import 'com.jolbox.bonecp.BoneCPDataSource)
(defn pool-db  [db-spec]
  (let  [{:keys  [classname subprotocol subname user password
                  init-pool-size max-pool-size idle-time partitions]} db-spec
         min-connections  (inc  (quot init-pool-size partitions))
         max-connections  (inc  (quot max-pool-size partitions))
         cpds  (doto  (BoneCPDataSource.)
                 (.setDriverClass classname)
                 (.setJdbcUrl  (str  "jdbc:" subprotocol  ":" subname))
                 (.setUsername user)
                 (.setPassword password)
                 (.setMinConnectionsPerPartition min-connections)
                 (.setMaxConnectionsPerPartition max-connections)
                 (.setPartitionCount partitions)
                 (.setStatisticsEnabled true)
                 (.setIdleMaxAgeInMinutes  (or idle-time 60)))]
    {:datasource cpds}))

(def type-maps
  (hash-map
    :long :integer
    :double :float
    :string :text))

(defn into-hashmap [ks vs]
  (into {} (map #(vector %1 %2) ks vs)))

(defn make-headers [colnames types]
  (assert (= (count colnames) (count types)) "colnames count should equal types'")
  (cons [:_primary_id :integer "PRIMARY KEY AUTOINCREMENT"]
    (map #(vector %1 (get type-maps %2)) colnames types)))

(defn to-sql [db-name table-name colnames types rows]
  (assert (= (count colnames) (-> rows first count)) "headers' dim should match row's")
  (let [pooled-db (pool-db (make-db-spec db-name))
        headers (make-headers colnames types)
        tag-row (partial into-hashmap colnames)
        rows* (map #(tag-row %) rows)
        create-table #(jdbc/db-do-commands pooled-db false 
                                           (apply (partial ddl/create-table table-name) %))
        insert! (partial jdbc/insert! pooled-db table-name)]
    (create-table headers)
    (doseq [row rows*]
      (insert! row))))
