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

(defn player-trapped?
  "Returns true if the player has been trapped by the murderer.

  There is a random chance that the player will be trapped by
  the murderer after entering a room, if the murderer is stalking
  the player."
  [is-stalking location target]
  (if (and
       is-stalking
       (has-entered-room location target)
       (> (rand-int 3) 1) true) ; 33% chance
    true
    false))


(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        location (:location player)
        target (destination-coords location dir)
        entity-at-target (get-entity-at world target)]
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
               (if (player-trapped? (:murderer-is-stalking (:flags world)) location target)
                 (-> world
                     (assoc-in [:flags :game-lost] true)
                     (assoc-in [:murder-case :lose-text] (get-lose-time world)))
                 world))))
