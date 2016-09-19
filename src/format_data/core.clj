(ns format-data.core
  (require [format-data.helpers :as hlp]
           [lazy-files.core :refer [lazy-read lazy-write]]))


(defn empty? [coll]
  (if (string? coll) (re-seq #"^\s*$" coll)
    (clojure.core/empty? coll)))

(defn numeric? [s]
  (if (empty? s) false
    (not (re-seq #"[^\d,.'-]+" s))))

(defn infere-string-type [s]
  (cond 
    (empty? s) :empty
    (not (numeric? s)) :string
    (re-seq #"^((([\d,]+.)|([\d']+.))\d+)$" s) :double
    :else :long))

(defn map-string-type [ss]
  (map infere-string-type ss))

(defn infere-type-candidates [sss])

(defn decide-type [ss])

(defn infere-cols-type [sss])
