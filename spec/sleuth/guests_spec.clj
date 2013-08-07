(ns sleuth.tile-spec
  (:require [speclj.core :refer :all]
            [sleuth.entities.guests :refer :all]))

(describe "get-guest-names"
          (with test-world (assoc-in {} [:entities :guests] (assoc {}
                                                                  :sondra (->Guest "Sondra Levinson" " " 0 [0 0] " ")
                                                                  :yolanda (->Guest "Yolanda Gould" " " 0 [0 0] " ")
                                                                  :joey (->Guest "Joey Fitzgibbon" " " 0 [0 0] " "))))

          (it "should return a sequence of the first names of all guests in the world."
              (should= '("Joey" "Yolanda" "Sondra") (get-guest-names @test-world))))
