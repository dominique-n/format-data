(ns format-data.helpers)


(defn empty? [coll]
  (if (string? coll) (re-seq #"^\s*$" coll)
    (clojure.core/empty? coll)))

(defn numeric? [s]
  (if (empty? s) false
    (not (re-seq #"[^-\d,\.'eE]" s))))
