(ns sleuth.entities.guests
  (:use [sleuth.world.rooms :only [random-coords random-room current-room in-room? get-rect
                                   get-current-guest]]
        [sleuth.world.items :only [get-item-rooms]]
        [sleuth.world.alibis :only [get-alibis]]
        [sleuth.utils :only [keyword-to-string keyword-to-name keyword-to-first-name]])
  (:require [clj-yaml.core :as yaml]))

; Data Structures --------------------------------------------------------------------------
(defrecord Guest [name alibi num-questions location description
                  is-staring-at-floor])

(def guest-names (atom nil))
(def personalized-names (atom nil))
(def guest-descriptions (atom nil))

; Guest Functions -----------------------------------------------------------------------------
(defn load-guests
  "Loads guest text from filename"
  [filename]
  (let [items-map {}];(yaml/parse-string (slurp "resources/guests.yaml"))]
    (reset! guest-names (map keyword (:guest-names items-map)))
    (reset! guest-descriptions (:guest-descriptions items-map))))

(defn random-name
  "Returns a random name from the given names list"
  [names]
  (rand-nth names))

(defn get-guests
  "Returns a list of guests from the given names with random locations.

  names is a vector of keywords."
  [names]
  (into {} (for [n names]
             [n (->Guest (keyword-to-name n) " " 0 [0 0] " " false)])))

(defn get-guest-names
  "Returns a list of the first names of the guests in the mansion."
  [world]
  (let [guests (get-in world [:entities :guests])]
    (into [] (for [[k v] guests]
               (keyword-to-first-name (:name v))))))

(defn random-guest-name
  "Returns a random keyword name from the list of guests in the mansion."
  [world]
  (let [guests (get-in world [:entities :guests])]
    (rand-nth (keys guests))))

(defn flip-current-stare-flag
  "Flips the is-staring-at-floor flag for the guest in the current room."
  [world]
  (let [guest-name (get-current-guest world)]
    (if guest-name
      (let [flag (not (:is-staring-at-floor (guest-name (:entities world))))]
        (assoc-in world [:entities :guests guest-name :is-staring-at-floor] flag))
      world)))

(defn update-guest-staring-flags
  "Flips the current guest's staring flag with a probability proportional to turn count."
  [world]
  (let [turn-count (get-in world [:murder-case :turn-count])]
    (cond
     (< (rand-int 300) turn-count)
     (flip-current-stare-flag world)
     :else world)))

(defn move-guests
  "Moves the guests in the world guestlist to the player's current room."
  [world]
  (let [guests (get-in world [:entities :guests])
        [player-x player-y] (get-in world [:entities :player :location])
        current-room (current-room world)
        rect (get-rect current-room)
        guest-x (+ (:x rect) 4)
        guest-y (cond
                 (= current-room :secret-passage) player-y
                 (in-room? [player-x (- player-y 1)] current-room) (- player-y 1)
                 :else (+ player-y 1))]
    (assoc-in world [:entities :guests]
              (zipmap (keys guests) (map #(assoc-in % [:location] [%2 guest-y])
                                         (vals guests) (range guest-x (+ guest-x 6)))))))

(defn move-murderer
  "Moves the murderer to the player's current room, if there is not already a guest in the room."
  [world]
  (if (not (nil? (get-current-guest world)))
    world
    (let [murderer (get-in world [:murder-case :murderer])
          [player-x player-y] (get-in world [:entities :player :location])
          murderer-x (if (in-room? [(+ player-x 1) player-y] (current-room world))
                       (+ player-x 1)
                       (- player-x 1))]
      (assoc-in world [:entities :guests murderer :location] [murderer-x player-y]))))

(defn ensure-guest-in-current-room
  [world]
  (if (not (nil? (get-current-guest world)))
    world
    (let [guest-name (random-guest-name world)
          room (current-room world)
          coords (random-coords room)
          description (rand-nth (room @guest-descriptions))]
      (as-> world world
            (assoc-in world [:entities :guests guest-name :room] room)
            (assoc-in world [:entities :guests guest-name :location] coords)
            (assoc-in world [:entities :guests guest-name :description] description)))))

(defn place-guest
  "Place the given guest at a random location in a random empty room."
  [world guest-name]
  (let [guests (get-in world [:entities :guests])
        rooms (into [] (for [[k v] guests] (:room v)))
        room (random-room rooms)
        coords (random-coords room)
        description (rand-nth (room @guest-descriptions))]
    (as-> world world
          (assoc-in world [:entities :guests guest-name :room] room)
          (assoc-in world [:entities :guests guest-name :location] coords)
          (assoc-in world [:entities :guests guest-name :description] description))))

(defn update-guest-locations
  "Updates locations of guests based on turn count."
  [world]
  (let [turn-count (get-in world [:murder-case :turn-count])]
    (cond
     (< (rand-int 300) turn-count)
     ; move guest to current room and have the guest stare at the floor
     (if (= (current-room world) (:room (:murder-case world)))
       (as-> world world
         (ensure-guest-in-current-room world)
         (assoc-in world [:entities :guests (get-current-guest world) :is-staring-at-floor] true))
       ; move random guest
       (place-guest world (random-guest-name world)))

     :else world)))

(defn place-guests
  "Moves all of the guests in guest-list.

  guest-list is a vector of keyword names."
  [guest-list world]
  (loop [guest-list guest-list
         world world]
    (if (empty? guest-list)
      world
      (do
        (let [guest-name (first guest-list)
              guest (get-guests [guest-name])
              world (assoc-in world [:entities :guests guest-name] (guest-name guest))
              world (place-guest world guest-name)]
          (recur (rest guest-list) world))))))

(defn create-guests
  [world]
  "Creates guests with their alibis"
  (let [names-list (if (:personalized (:flags world))
                       (apply list @personalized-names)
                       @guest-names)
        names (shuffle names-list)
        [victim
         murderer
         alone
         suspect1
         suspect2
         suspect3
         suspect4] names
        rooms (shuffle (get-item-rooms))
        [room1 room2 room3] rooms
        guest-list (apply list (remove #{victim} names))
        world (place-guests guest-list world)]
    (as-> world world
        (assoc-in world [:murder-case :victim] victim)
        (assoc-in world [:murder-case :murderer] murderer)
        (assoc-in world [:entities :guests murderer :alibi] :murderer)
        (assoc-in world [:entities :guests alone :alibi] :alone)
        (assoc-in world [:entities :guests suspect1 :alibi] suspect2)
        (assoc-in world [:entities :guests suspect2 :alibi] suspect1)
        (assoc-in world [:entities :guests suspect3 :alibi] suspect4)
        (assoc-in world [:entities :guests suspect4 :alibi] suspect3)
        (assoc-in world [:entities :guests suspect1 :alibi-room] room1)
        (assoc-in world [:entities :guests suspect2 :alibi-room] room1)
        (assoc-in world [:entities :guests suspect3 :alibi-room] room2)
        (assoc-in world [:entities :guests suspect4 :alibi-room] room2)
        (assoc-in world [:entities :guests alone :alibi-room] room3)
        (assoc-in world [:entities :guests]
                  (get-alibis (get-in world [:entities :guests]))))))
