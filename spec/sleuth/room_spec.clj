(ns sleuth.room-spec
  (:require [speclj.core :refer :all]
            [sleuth.world.rooms :refer :all])
  (:use [sleuth.world.tiles :only [get-tile]]))

(describe "in-rect?"
          (it "should return true if coordinates are in the rect"
              (should (in-rect? [1 10] (:living-room room-rects))))

          (it "should return false if coordinates are not in the rect"
              (should-not (in-rect? [100 200] (:living-room room-rects)))))

(describe "current-room"
          (with world (assoc-in {} [:entities :player :location] [1 10]))

          (it "should return the correct room, given valid coordinates"
              (should= :living-room (current-room @world)))

          (it "should return a keyword"
              (should (keyword? (current-room @world)))))

(describe "get-doorway"
          (it "should return nil if room is a hallway"
              (should-be-nil (get-doorway :main-hall)))

          (it "should return the correct coordinates if room is a doorway"
              (should= [15 3] (get-doorway :doorway-dining-room)))

          (it "should return the correct coordinates of the doorway"
              (should= [15 11] (get-doorway :living-room))))

(describe "lock-current-room"
          (with world (lock-current-room (assoc-in {} [:entities :player :location] [1 10])))

          (it "should place door in doorway of current room"
                (should= :door (:kind (get-tile @world [15 11])))))