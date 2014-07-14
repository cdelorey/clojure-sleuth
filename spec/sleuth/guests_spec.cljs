(ns sleuth.tile-spec
  (:require [specljs.core]
            [sleuth.entities.guests :refer [->Guest get-guest-names]])
	(:require-macros [specljs.core :refer [before with describe it should=]]))

(before
 (with test-world (assoc-in {} [:entities :guests] (assoc {}
                                                                  :sondra (->Guest "Sondra Levinson" " " 0 [0 0] " " false)
                                                                  :yolanda (->Guest "Yolanda Gould" " " 0 [0 0] " " false)
                                                                  :joey (->Guest "Joey Fitzgibbon" " " 0 [0 0] " " false))))
 (describe "get-guest-names"
           (it "should return a sequence of the first names of all guests in the world."
               (should= '("Joey" "Yolanda" "Sondra") (get-guest-names @test-world)))))
