(ns sleuth.entities.player
  (:use [sleuth.coords :only [destination-coords]]
        [sleuth.world.tiles :only [get-entity-at is-empty?]]
        [sleuth.world.portals :only [portal? get-portal secret-passage? get-passage in-secret-passage]]
        [sleuth.world.rooms :only [has-entered-room]]
        [sleuth.world.alibis :only [get-lose-time]]
        [sleuth.utils :only [and-as->]]))

; Data Structures -------------------------------------------------------------
(defrecord Player [id glyph location])


; Player Functions ------------------------------------------------------------
(defn make-player [world]
  (->Player :player "@" [2 2]))

(defn get-player-location [world]
  (get-in world [:entities :player :location]))

(defn can-move?
  [dest world]
  (is-empty? dest world))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        location (:location player)
        target (destination-coords location dir)
        entity-at-target (get-entity-at world target)]
    ;testing (println (str "Location: " (:location player)))
    ;testing (println (str "Target: " target))
    (and-as-> world world
              ; handle movement
              (cond
               (portal? target) (assoc-in world [:entities :player :location] (get-portal target))
               (secret-passage? target world) (let [location (get-passage target world)]
                                                (swap! in-secret-passage #(if (= % true)
                                                                            false
                                                                            true))
                                                (assoc-in world [:entities :player :location] location))
               (can-move? target world) (assoc-in world [:entities :player :location] target)
               :else world)
              ; handle entered-room flag
              (if (and (:murderer-is-stalking (:flags world))
                       (has-entered-room location target))
                ; pull this out into separate function
                (-> world
                    (assoc-in [:flags :game-lost] true)
                    (assoc-in [:murder-case :lose-text] (get-lose-time world)))
                world))))
