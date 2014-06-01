(ns sleuth.world.portals
  (:use [sleuth.world.rooms :only [random-coords]]))

(def in-secret-passage (atom false))

(def portals {[16 16] [60 15]
              [17 16] [61 15]
              [18 16] [62 15]
              [60 16] [16 15]
              [61 16] [17 15]
              [62 16] [18 15]})

(def passages {[1 7] [1 8]
               [2 7] [2 8]
               [3 7] [3 8]
               [4 7] [4 8]
               [5 7] [5 8]
               [6 7] [6 8]
               [7 7] [7 8]
               [8 7] [8 8]
               [9 7] [9 8]
               [10 7] [10 8]
               [11 7] [11 8]
               [12 7] [12 8]
               [13 7] [13 8]
               [14 7] [14 8]})

; Portal Functions -----------------------------------------------------------
(defn portal? 
  "Return true if [x y] is a key in portals."
  [[x y]]
  (if (contains? portals [x y])
    true
    false))

(defn get-portal
  "Return the value in portals for the key [x y]"
  [[x y]]
  (get portals [x y]))

(defn random-passages
  "Insert one random passage location for each side of the secret room into world."
  [world]
  (let [int1 (+ (rand-int 14) 1)
        key1 [int1 7]
        val1 [int1 8]
        int2 (+ (rand-int 14) 1)
        key2 [int2 9]
        val2 [int2 8]]
    (-> world
        (assoc-in [:secret-passages key1] val1)
        (assoc-in [:secret-passages key2] val2))))

(defn secret-passage?
  "Return true if [x y] leads to the secret passage in world."
  [[x y] world]
  (if (contains? (:secret-passages world) [x y])
    true
    false))

(defn get-passage
  "Return the other side of the passage [x y] in world"
  [[x y] world]
  (if (= @in-secret-passage true)
    (random-coords)
    (get-in world [:secret-passages [x y]])))
