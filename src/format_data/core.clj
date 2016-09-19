(ns format-data.core
  (require [format-data.helpers :as hlp]))


(defn empty? [coll]
  (if (string? coll) (re-seq #"^\s*$" coll)
    (clojure.core/empty? coll)))

(defn numeric? [s]
  (if (empty? s) false
    (not (re-seq #"[^\d,.'-]+" s))))

(defn infer-string-type [s]
  (cond 
    (empty? s) :empty
    (not (numeric? s)) :string
    (re-seq #"^((([\d,]+.)|([\d']+.))\d+)$" s) :double
    :else :long))

(defn map-string-type [ss]
  (map infer-string-type ss))

(defn infer-type-candidates [sss]
  (let [n (count (first sss))
        conj-types (fn [acc types] (map #(conj %1 %2) acc types))]
    (reduce conj-types 
            (repeatedly n (fn [] #{}))
            (map map-string-type sss) )))

(defn infer-type [tss]
  (cond
    (:string tss) :string
    (:double tss) :double
    (:long tss) :long
    :else :string))

(defn infer-cols-type [sss]
  (map infer-type (infer-type-candidates sss)))
