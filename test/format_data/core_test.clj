(ns format-data.core-test
  (:require [midje.sweet :refer :all]
            [format-data.core :refer :all]))


(let [sss [["1.0" "2" "1lo2l3" ""]
           ["1.0" "2.0" "1.0" " "]
           ["1.0" "2" "1,111$" ""]]]

(facts "About `parse-int"
       (parse-int "11") => 11
       (parse-int "1.2") => falsey
       (parse-int "") => falsey
       )

(facts "About `parse-double"
       (parse-double "1.1") => 1.1
       (parse-double "1") => falsey
       (parse-double "") => falsey
       )

(facts "About `noisy-numeric?"
       (noisy-numeric? "") => falsey
       (noisy-numeric? " ") => falsey
       (noisy-numeric? "1") => truthy
       (noisy-numeric? "111,111") => truthy
       (noisy-numeric? "111'111") => truthy
       (noisy-numeric? "111-111") => truthy
       (noisy-numeric? "$111'111") => truthy
       (noisy-numeric? "111'111$") => truthy
       (noisy-numeric? "1.2") => truthy
       (noisy-numeric? "111,111.2") => truthy
       (noisy-numeric? "111'111.2") => truthy
       (noisy-numeric? "111-111.2") => truthy
       (noisy-numeric? "$111,111.2") => truthy
       (noisy-numeric? "$111,111.2$") => truthy
       (noisy-numeric? "1.2a") => falsey
       (noisy-numeric? "1.2a7") => falsey

       (noisy-numeric? "1.2e7") => truthy
       (noisy-numeric? "1.2e-7") => truthy
       (noisy-numeric? "111.222e77") => truthy
       (noisy-numeric? "111.222e-77") => truthy

       (noisy-numeric? "1.2E7") => truthy
       (noisy-numeric? "1.2E-7") => truthy
       (noisy-numeric? "111.222E77") => truthy
       (noisy-numeric? "111.222E-77") => truthy
       )

(facts :infer-string-type
       (fact "`infer-string-type should infer non-emtpy strings"
             (infer-string-type nil) => (throws AssertionError)
             (infer-string-type "1") => :long
             (infer-string-type "1-1-1") => :noisy-numeric
             (infer-string-type "1.1.1") => :noisy-numeric
             (infer-string-type "1-1.1") => :noisy-numeric
             (infer-string-type "1,1,1") => :noisy-numeric
             (infer-string-type "111-1111.0") => :noisy-numeric

             (infer-string-type "1.0") => :double
             (infer-string-type "111,1111.0") => :noisy-numeric
             (infer-string-type "111'1111.0") => :noisy-numeric

             (infer-string-type "ab") => :string
             (infer-string-type "1ab") => :string
             (infer-string-type "a1b") => :string
             (infer-string-type "ab1") => :string
             )

       (fact "`infer-string-type should infer :empty for empty strings"
             (infer-string-type "") => :empty
             (infer-string-type " ") => :empty
             ))

  (facts "About `infer-row-types"
         (infer-row-types (first sss)) => (just [:double :long :string :empty])
         (infer-row-types (last sss)) => (just [:double :long :noisy-numeric :empty])
         )

  (facts "About `infer-types-candidates"
         (infer-types-candidates sss)) => (just [{:double 3}
                                                {:double 2 :long 1}
                                                {:string 1 :double 1 :noisy-numeric 1}
                                                {:empty 2}])

  (facts :infer-types 
         (fact "`infer-types prefer String when String detected"
               (infer-types #{:double :long :string :empty}) => :string
               (infer-types #{:double :string}) => :string
               (infer-types #{:long :string}) => :string
               (infer-types #{:empty :string}) => :string
               (infer-types #{:noisy-numeric :string}) => :string
               )

         (fact "`infer-types prefers String when no type detected"
               (infer-types #{:empty}) => :string
               )

         (fact "`infer-types prefers String whenever noisy numbers detected"
               (infer-types #{:noisy-numeric}) => :string
               (infer-types #{:noisy-numeric :empty}) => :string
               (infer-types #{:noisy-numeric :double }) => :string
               (infer-types #{:noisy-numeric :long }) => :string
               (infer-types #{:noisy-numeric :double :long}) => :string
               (infer-types #{:noisy-numeric :double :long :empty}) => :string
               )

         (fact "`infer-types prefers Double when mixed numeric detected"
               (infer-types #{:double :long :empty}) => :double
               )

         (fact "`infer-types should pick double when mixed with empty"
               (infer-types #{:long :empty}) => :long
               ))

  (facts "About `infer-cols-type"
         (infer-cols-type sss) => [:double :double :string :string] 
         )
  )


