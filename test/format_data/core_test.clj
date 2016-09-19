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

(fact "`infer-string-type should infer non-emtpy strings"
       (infer-string-type "1.0") => :double
       (infer-string-type "1") => :long
       (infer-string-type "ab") => :string
       (infer-string-type "1ab") => :string
       (infer-string-type "a1b") => :string
       (infer-string-type "ab1") => :string
       )

(fact "`infer-string-type should infer :empty for empty strings"
      (infer-string-type "") => :empty
      (infer-string-type " ") => :empty
      )

(facts "About `map-string-type"
       (map-string-type ["1.0" "2" "1lo2l3" ""]) => (just [:double :long :string :empty])
       )

(facts "About `infer-type-candidates"
       (let [sss [["1.0" "2" "1lo2l3" ""]
                  ["1.0" "2.0" "1.0" " "]]]
         (infer-type-candidates sss)) => (just [#{:double} #{:double :long}
                                              #{:string :double} #{:empty}])
       )

(fact "`decide-type prefer String when String detected"
      (decide-type #{:double :long :string :empty}) => :string
      (decide-type #{:double :string}) => :string
      (decide-type #{:long :string}) => :string
      (decide-type #{:empty :string}) => :string
      )

(fact "`decide-type prefers String when no type detected"
      (decide-type #{:empty}) => :string
      )

(fact "`decide-type prefers Double when mixed numeric detected"
      (decide-type #{:double :long :empty}) => :double
      )
