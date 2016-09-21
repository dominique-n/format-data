(ns format-data.helpers-test
  (require [midje.sweet :refer :all]
           [format-data.helpers :refer :all]))


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

