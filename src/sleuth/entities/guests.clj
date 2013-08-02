(ns sleuth.entities.guests
  (:use [sleuth.world.rooms :only [random-coords random-room current-room in-room?]]
        [sleuth.world.items :only [get-item-rooms]]
        [sleuth.world.alibis :only [get-alibis]]
        [sleuth.entities.player :only [get-player-location]]
        [sleuth.utils :only [keyword-to-string keyword-to-name]])
  (:require [clj-yaml.core :as yaml]))

; Data Structures --------------------------------------------------------------------------
(defrecord Guest [name alibi num-questions location description])

(def guest-names (promise))
(def guest-descriptions (promise))

; Guest Functions -----------------------------------------------------------------------------
(defn load-guests
  "Loads guest text from filename"
  [filename]
  (let [items-map (yaml/parse-string (slurp "resources/guests.yaml"))]
    (deliver guest-names (map keyword (:guest-names items-map)))
    (deliver guest-descriptions (:guest-descriptions items-map))))

(defn random-name
  "Returns a random name from the given names list"
  [names]
  (rand-nth names))

(defn get-guests
  "Returns a list of guests from the given names with random locations.

  names is a vector of keywords."
  [names]
  (into {} (for [n names]
             [n (->Guest (keyword-to-name n) " " 0 [0 0] " ")])))

(defn move-guests
  "Moves the guests in the world guestlist to the player's current room"
  [world]
  (let [guests (get-in world [:entities :guests])
        [player-x player-y] (get-player-location world)
        current-room (current-room world)
        guest-y (if (in-room? [player-x (+ 1 player-y)] current-room)
                  (+ 1 player-y)
                  (- 1 player-y))
        guest-x (if (in-room? [(- player-x 2) guest-y] current-room)
                  (- player-x 2)
                  player-x)] ;TODO: these positions will not work for secret passage fix!
    (assoc-in world [:entities :guests]
              (zipmap (keys guests) (map #(assoc-in % [:location] [%2 guest-y])
                                         (vals guests) (range guest-x (+ guest-x 6)))))))


(defn place-guest
  [guest-name guest world]
  (let [guests (get-in world [:entities :guests])
        rooms (into [] (for [[k v] (get-in world [:entities :guests])]
                         (:room v)))
        old-room (get-in guests [guest-name :room])
        room (random-room rooms)
        coords (random-coords room)
        description (rand-nth (room @guest-descriptions))]
    (as-> world world
          (assoc-in world [:entities :guests guest-name :room] room)
          (assoc-in world [:entities :guests guest-name :location] coords)
          (assoc-in world [:entities :guests guest-name :description] description))))

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
              world (place-guest guest-name guest world)]
          (recur (rest guest-list) world))))))

(defn create-guests
  [world]
  "Creates guests with their alibis"
  (let [names (shuffle @guest-names)
        [victim
         murderer
         alone
         suspect1
         suspect2
         suspect3
         suspect4] names
        rooms (shuffle (get-item-rooms))
        [room1 room2 room3] rooms
        guest-list (remove #{victim} @guest-names)
        world (place-guests guest-list world)]
    ; testing
    (println "Room1: " room1 " Guests: " suspect1 " " suspect2)
    (println "Room2: " room2 " Guests: " suspect3 " " suspect4)
    (println "Room3: " room3 " Guest: " alone)
    (println "HELLO")
    (println "Murderer: " murderer)
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
