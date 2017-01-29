(ns format-data.db
  (require [clojure.java.jdbc :as jdbc]
           [java-jdbc.ddl :as ddl]
           [java-jdbc.sql :as sql])
  )


(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol  "sqlite"
   :subname     "dev-resources/database.db"
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

(def pooled-db (pool-db db-spec))

;(def pooled-db (delay (pool-db db-spec)))
;(defn db-connection [] @pooled-db)


(jdbc/db-do-commands pooled-db false (ddl/drop-table :blog_posts))
(jdbc/db-do-commands pooled-db false
                     (ddl/create-table
                       :blog_posts
                       [:id :integer "PRIMARY KEY AUTOINCREMENT"]
                       [:title  "varchar(255)"  "NOT NULL"]
                       [:body :text]))
;; ->  (0)


(jdbc/insert! pooled-db
                    :blog_posts 
                    [:title :body]
                    ["title0" "good news"] ["title1" "good news"])
(jdbc/insert! pooled-db
              :blog_posts
              {:title  "My first post!" :body  "This is going to be good!"}
              {:title  "My second post!" :body  "This is going to be really good!"})
;; -> ({:body "This is going to be good!", :title "My first post!", :id 1})

(jdbc/query pooled-db
            (sql/select * :blog_posts  (sql/where {:title  "My first post!"})))
(jdbc/query pooled-db (sql/select * :blog_posts ))
;; -> ({:body "This is going to be good!", :title "My first post!", :id 1}))
