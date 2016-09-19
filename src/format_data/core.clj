(ns format-data.core
  (require [format-data.helpers :as hlp]
           [lazy-files.core :refer [lazy-read lazy-write]]))


(defn numeric? [s]
  (not (re-seq #"[^\d,.'-]+" s)))

(defn infere-string-type [s]
  (cond 
    (not (numeric? s)) :string
    (re-seq #"^((([\d,]+.)|([\d']+.))\d+)$" s) :double
    :else :long))

(defn map-string-type [ss])

(defn infere-type-candidates [sss])

(defn decide-type [ss])

(defn infere-cols-type [sss])
