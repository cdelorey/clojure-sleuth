(ns sleuth.tile-spec
  (:require [speclj.core :refer :all]
            [sleuth.entities.guests :refer :all]))

(before
 (with test-world (assoc-in {} [:entities :guests] (assoc {}
                                                                  :sondra (->Guest "Sondra Levinson" " " 0 [0 0] " " false)
                                                                  :yolanda (->Guest "Yolanda Gould" " " 0 [0 0] " " false)
                                                                  :joey (->Guest "Joey Fitzgibbon" " " 0 [0 0] " " false))))
 (describe "get-guest-names"
           (it "should return a sequence of the first names of all guests in the world."
               (should= '("Joey" "Yolanda" "Sondra") (get-guest-names @test-world)))))
