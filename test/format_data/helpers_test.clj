(ns format-data.helpers-test
  (require [midje.sweet :refer :all]
           [format-data.helpers :refer :all]))


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
         (numeric? "1.2a7") => falsey

         (numeric? "1.2e7") => truthy
         (numeric? "1.2e-7") => truthy
         (numeric? "111.222e77") => truthy
         (numeric? "111.222e-77") => truthy

         (numeric? "1.2Ee7") => truthy
         (numeric? "1.2Ee-7") => truthy
         (numeric? "111.222E77") => truthy
         (numeric? "111.222E-77") => truthy
         )

