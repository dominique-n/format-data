(ns format-data.core
  (require [format-data.helpers :as hlp]
           [lazy-files.core :refer [lazy-read lazy-write]]))


(defn numeric? [s]
  (not (re-seq #"[^\d,.'-]+" s)))

(defn get-string-type [s]
  )

(defn map-string-type [ss])

(defn infere-type-candidates [sss])

(defn decide-type [ss])

(defn infere-cols-type [sss])
