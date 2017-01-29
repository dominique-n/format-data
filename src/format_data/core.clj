(ns format-data.core
  (require [format-data.helpers :as hlp]))


(defn noisy-numeric? [s]
  "check whether digits are polluted by non-language characters"
  (seq 
    (re-seq #"^\d+(e\d|E\d){0,1}\d*$"
            (clojure.string/replace s #"\W" ""))))

(defn parse-int [s]
  (try (Integer. s) (catch NumberFormatException e)))

(defn parse-double [s]
  (and (not (parse-int s)) 
    (try (Double. s) (catch NumberFormatException e))))

(defn infer-string-type [s]
  (assert (string? s) "expect a string")
  (let [s (clojure.string/replace s #"\s+" "")]
    (cond 
      (parse-int s) :long
      (parse-double s) :double
      (noisy-numeric? s) :noisy-numeric
      (empty? s) :empty
      :else :string)))

(defn infer-row-types [ss]
  (map infer-string-type ss))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;integration

(defn infer-types-candidates [sss]
  (let [n (count (first sss))
        conj-types (fn [acc types] 
                     (mapv #(assoc %1 %2 (inc (get %1 %2 0))) 
                          acc types))]
    (reduce conj-types 
            (vec (repeatedly n hash-map))
            (map infer-row-types sss) )))

(defn infer-type [tss]
  (cond
    (:string tss) :string
    (:double tss) :double
    (:long tss) :long
    :else :string))

(defn infer-cols-type [sss]
  (map #(-> % keys set infer-type)
       (infer-types-candidates sss)))
