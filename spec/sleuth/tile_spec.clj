(ns sleuth.tile-spec
  (:require [speclj.core :refer :all]
            [sleuth.world.tiles :refer :all]))

(describe "set-tile"
          (with test-world (set-tile {:tiles []} [0 0] :door))

          (it "should set the given tile to the given tile type"
            (should= :door (:kind (get-in (:tiles @test-world) [0 0])))))
