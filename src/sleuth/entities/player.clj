(ns sleuth.entities.player
  (:use [sleuth.entities.core :only [Entity]]))

(defrecord Player [id glyph location])

(extend-type Player Entity
  (tick [this world]
    world))

(defn make-player [world]
  (->Player :player "@" [21 17])) 
