(ns sleuth.entities.player
  (:use [sleuth.coords :only [destination-coords]]
        [sleuth.world.core :only [get-entity-at is-empty?]]
        [sleuth.world.rooms :only [portal? get-portal]]))

(defrecord Player [id glyph location])

(defn make-player [world]
  (->Player :player "@" [2 2])) 

(defn can-move?
  [dest world]
  (is-empty? dest world))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (println (str "Location: " (:location player)))
    (println (str "Target: " target))
    (cond
      (portal? target) (assoc-in world [:entities :player :location] (get-portal target))
      (can-move? target world) (assoc-in world [:entities :player :location] target)
      :else world)))
