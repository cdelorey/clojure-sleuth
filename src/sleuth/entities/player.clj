(ns sleuth.entities.player
  (:use [sleuth.entities.core :only [Entity]]
        [sleuth.entities.core :only [add-aspect]]
        [sleuth.entities.aspects.mobile :only [Mobile move can-move?]]
        [sleuth.coords :only [destination-coords]]
        [sleuth.world.core :only [get-entity-at]]))

(defrecord Player [id glyph location])

(extend-type Player Entity
  (tick [this world]
    world))

(add-aspect Player Mobile)

(defn make-player [world]
  (->Player :player "@" [42 1])) 

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
      (can-move? player target world) (move player target world)
      :else world)))
