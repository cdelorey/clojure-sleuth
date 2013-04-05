(ns sleuth.entities.player
  (:use [sleuth.coords :only [destination-coords]]
        [sleuth.world.core :only [get-entity-at is-empty?]]
        [sleuth.world.portals :only [portal? get-portal secret-passage? get-passage in-secret-passage]]))

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
    ;testing (println (str "Location: " (:location player)))
    ;testing (println (str "Target: " target))
    (cond
     (portal? target) (assoc-in world [:entities :player :location] (get-portal target))
     (secret-passage? target world) (let [location (get-passage target world)]
                                      (swap! in-secret-passage #(if (= % true)
                                                                  false
                                                                  true))
                                      (assoc-in world [:entities :player :location] location))
     (can-move? target world) (assoc-in world [:entities :player :location] target)
     :else world)))
