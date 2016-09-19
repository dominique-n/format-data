(ns format-data.core-test
  (:require [midje.sweet :refer :all]
            [format-data.core :refer :all]))


(facts "About `numeric?"
       (numeric? "1") => truthy
       (numeric? "111,111") => truthy
       (numeric? "111'111") => truthy
       (numeric? "111-111") => truthy
       (numeric? "$111'111") => falsey
       (numeric? "111'111$") => falsey
       (numeric? "1.2") => truthy
       (numeric? "111,111.2") => truthy
       (numeric? "111'111.2") => truthy
       (numeric? "111-111.2") => truthy
       (numeric? "$111,111.2") => falsey
       (numeric? "$111,111.2$") => falsey
       (numeric? "1.2a") => falsey
       )

(future-fact "`get-string-type should infere non-emtpy strings"
       (get-string-type "1.0") => :double
       (get-string-type "1") => :long
       (get-string-type "ab") => :string
       (get-string-type "1ab") => :string
       (get-string-type "a1b") => :string
       (get-string-type "ab1") => :string
       )

(future-fact "`get-string-type should infere :empty for empty strings"
      (get-string-type "") => :empty
      (get-string-type " ") => :empty
      )

(future-facts "About `map-string-type"
       (map-string-type ["1.0" "2" "1lo2l3" ""]) => (just [:double :long :string :empty])
       )

(future-facts "About `infere-type-candidates"
       (let [sss [["1.0" "2" "1lo2l3" ""]
                  ["1.0" "2.0" "1.0" ""]]]
         (infere-type-candidates sss)) => (just [#{:double} #{:double :long}
                                              #{:string :double} #{:empty}])
       )

(future-fact "`decide-type prefer String when String detected"
      (decide-type #{:double :long :string :empty}) => :string
      (decide-type #{:double :string}) => :string
      (decide-type #{:long :string}) => :string
      (decide-type #{:empty :string}) => :string
      )

(future-fact "`decide-type prefers String when no type detected"
      (decide-type #{:empty}) => :string
      )

(future-fact "`decide-type prefers Double when mixed numeric detected"
      (decide-type #{:double :long :empty}) => :double
      )
