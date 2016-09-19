(ns format-data.core-test
  (:require [midje.sweet :refer :all]
            [format-data.core :refer :all]))


(fact "`get-string-type should infere non-emtpy strings"
       (get-string-type "1.0") => java.lang.Double
       (get-string-type "1") => java.lang.Long
       (get-string-type "ab") => java.lang.String
       (get-string-type "1ab") => java.lang.String
       (get-string-type "a1b") => java.lang.String
       (get-string-type "ab1") => java.lang.String
       )

(future-fact "`get-string-type should infere nil for empty strings"
      (get-string-type "") => nil
      (get-string-type " ") => nil
      )

(future-facts "About `map-string-type"
       (map-string-type ["1.0" "2" "1lo2l3" ""]) => (just [java.lang.Double java.lang.Long java.lang.String nil])
       )

(future-facts "About `infere-type-candidates"
       (let [sss [["1.0" "2" "1lo2l3" ""]
                  ["1.0" "2.0" "1.0" ""]]]
         (infere-type-candidates sss)) => (just [#{java.lang.Double} #{java.lang.Double java.lang.Double}
                                              {#java.lang.String java.lang.Double} #{nil}])
       )

(future-fact "`decide-type prefer String when String detected"
      (decide-type #{java.lang.Double java.lang.Long java.lang.String nil}) => java.lang.String
      (decide-type #{java.lang.Double java.lang.String}) => java.lang.String
      (decide-type #{java.lang.Long java.lang.String}) => java.lang.String
      (decide-type #{nil java.lang.String}) => java.lang.String
      )

(future-fact "`decide-type prefers String when no type detected"
      (decide-type #{nil}) => java.lang.String
      )

(future-fact "`decide-type prefers Double when mixed numeric detected"
      (decide-type #{java.lang.Double java.lang.Long nil}) => java.lang.Double
      )
