(ns format-data.core-test
  (:require [midje.sweet :refer :all]
            [format-data.core :refer :all]))


(let [sss [["1.0" "2" "1lo2l3" ""]
           ["1.0" "2.0" "1.0" " "]]]

  (fact "`infer-string-type should infer non-emtpy strings"
        (infer-string-type "1") => :long
        (infer-string-type "1-1-1") => :long
        (infer-string-type "1.1.1") => :long
        (infer-string-type "1-1.1") => :long
        (infer-string-type "1,1,1") => :long
        (infer-string-type "111-1111.0") => :long

        (infer-string-type "1.0") => :double
        (infer-string-type "111,1111.0") => :double
        (infer-string-type "111'1111.0") => :double

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
         (infer-type-candidates sss)) => (just [{:double 2} {:double 1 :long 1}
                                                {:string 1 :double 1} {:empty 2}])


  (fact "`infer-type prefer String when String detected"
        (infer-type #{:double :long :string :empty}) => :string
        (infer-type #{:double :string}) => :string
        (infer-type #{:long :string}) => :string
        (infer-type #{:empty :string}) => :string
        )

  (fact "`infer-type prefers String when no type detected"
        (infer-type #{:empty}) => :string
        )

  (fact "`infer-type prefers Double when mixed numeric detected"
        (infer-type #{:double :long :empty}) => :double
        )

  (fact "`infer-type should pick double when mixed with empty"
        (infer-type #{:long :empty}) => :long
        )

  (facts "About `infer-cols-type"
         (infer-cols-type sss) => [:double :double :string :string] 
         )
  )


