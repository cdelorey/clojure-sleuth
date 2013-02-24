(ns sleuth.entities.aspects.mobile
  (:use [sleuth.entities.core :only [defaspect]]
        [sleuth.world.core :only [is-empty?]]))

(defaspect Mobile
  (move [this dest world]
        {:pre [(can-move? this dest world)]}
        (assoc-in world [:entities (:id this) :location] dest))
  (can-move? [this dest world]
             (is-empty? world dest)))
