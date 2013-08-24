(ns sleuth.entities.player
  (:use [sleuth.coords :only [destination-coords]]
        [sleuth.world.tiles :only [is-empty?]]
        [sleuth.world.alibis :only [get-lose-time]]
        [sleuth.world.portals :only [portal? get-portal secret-passage? get-passage in-secret-passage]]
        [sleuth.world.rooms :only [has-entered-room]]
        [sleuth.entities.guests :only [update-guest-staring-flags update-guest-locations]]))

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

(defn check-for-trap
  "Returns updated world if player has been trapped by murderer.

  There is a random chance that the player will be trapped by
  the murderer after entering a room, if the murderer is stalking
  the player."
  [world]
  (if (and
       (:murderer-is-stalking (:flags world))
       (> (rand-int 3) 1) true) ; 33% chance
    (-> world
                     (assoc-in [:flags :game-lost] true)
                     (assoc-in [:murder-case :lose-text] (get-lose-time world)))
    world))

(defn handle-player-movement
  [world location target]
  "Moves player from location to target if possible."
  (cond
    (portal? target) (assoc-in world [:entities :player :location] (get-portal target))
    (secret-passage? target world) (let [location (get-passage target world)]
                                     (swap! in-secret-passage #(if (= % true)
                                                                 false
                                                                 true))
                                     (assoc-in world [:entities :player :location] location))
    (can-move? target world) (assoc-in world [:entities :player :location] target)
    :else world))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        location (:location player)
        target (destination-coords location dir)
        new-world (handle-player-movement world location target)]
    (if (has-entered-room location target)
      (as-> new-world new-world
            (update-guest-staring-flags new-world)
            (update-guest-locations new-world)
            (check-for-trap new-world)
            (assoc-in new-world [:flags :player-entered-room] false))
      new-world)))
