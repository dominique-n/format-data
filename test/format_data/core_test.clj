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

  (facts "About `infer-type-candidates"
         (infer-type-candidates sss)) => (just [{:double 3}
                                                {:double 2 :long 1}
                                                {:string 1 :double 1 :noisy-numeric 1}
                                                {:empty 2}])


  (facts :infer-type 
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
               ))

  (facts "About `infer-cols-type"
         (infer-cols-type sss) => [:double :double :string :string] 
         )
  )


