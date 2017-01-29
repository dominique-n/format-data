(ns format-data.helpers)


(defn numeric? [s]
  (if (empty? s) false
    (not (re-seq #"[^-\d,\.'eE]" s))))
