(ns format-data.core-test
  (:require [midje.sweet :refer :all]
            [format-data.core :refer :all]))


(facts "About `empty?"
       (empty? "") => truthy
       (empty? " ") => truthy
       (empty? "1") => falsey
       )

(facts "About `numeric?"
       (numeric? "") => falsey
       (numeric? " ") => falsey
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

(fact "`infere-string-type should infere non-emtpy strings"
       (infere-string-type "1.0") => :double
       (infere-string-type "1") => :long
       (infere-string-type "ab") => :string
       (infere-string-type "1ab") => :string
       (infere-string-type "a1b") => :string
       (infere-string-type "ab1") => :string
       )

(fact "`infere-string-type should infere :empty for empty strings"
      (infere-string-type "") => :empty
      (infere-string-type " ") => :empty
      )

(facts "About `map-string-type"
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
