(ns sleuth.world.alibis
  (:use [sleuth.utils :only [keyword-to-first-name keyword-to-string]]
        [sleuth.world.rooms :only [random-room current-room]])
  (:require [clj-yaml.core :as yaml]))

; Alibi components ----------------------------------------------------------------------------

; used at beginning of an alibi the first time a suspect is asked.
(def openers (promise))
; used at beginning of an alibi when the guest has already been asked
(def repeat-openers (promise))
; Alibis
(def alibis (promise))
; alone alibi
(def alone-alibi (promise))
; Additional things guests will say when asked the first time.
(def additions (promise))
; Additional comments by guest who was alone all night.
(def alone-additions (promise))
; Additional things guests will say when asked for the first time
; that include an accusation of another guest.
(def accusations (promise))
; said after a repeat opener
(def repeats (promise))
; said when a guest refuses to answer questions
(def refuse (promise))
; said the second time a guest is asked
(def finished (promise))
; text displayed when player loses from questioning murderer too much
(def lose-questioning (promise))
; text displayed when player loses from running out of time
(def lose-time (promise))

;when staring at floor
;"%s looks up from the floor and says,"

; Alibi Functions -----------------------------------------------------------------------------
(defn load-alibis
  "Loads alibi components from filename"
  [filename]
  (let [alibis-map (yaml/parse-string (slurp filename))]
    (deliver openers (:openers alibis-map))
    (deliver repeat-openers (:repeat-openers alibis-map))
    (deliver alibis (:alibis alibis-map))
    (deliver alone-alibi (:alone-alibi alibis-map))
    (deliver additions (:additions alibis-map))
    (deliver alone-additions (:alone-additions alibis-map))
    (deliver accusations (:accusations alibis-map))
    (deliver repeats (:repeats alibis-map))
    (deliver refuse (:refuse alibis-map))
    (deliver finished (:finished alibis-map))
    (deliver lose-questioning (:lose-questioning alibis-map))
    (deliver lose-time (:lose-time alibis-map))))

(defn get-lose-questioning
  "Returns the lose-questioning text with the proper values filled in"
  [world]
  (let [murderer (keyword-to-first-name (get-in world [:murder-case :murderer]))
        victim (keyword-to-first-name (get-in world [:murder-case :victim]))
        room (keyword-to-string (get-in world [:murder-case :room]))
        weapon (keyword-to-string (get-in world [:murder-case :weapon]))]
    (format (first @lose-questioning) murderer victim room weapon)))

(defn get-lose-time
  "Returns the lose-time text with the proper values filled in"
  [world]
  (let [murderer (keyword-to-first-name (get-in world [:murder-case :murderer]))
        victim (keyword-to-first-name (get-in world [:murder-case :victim]))
        room (keyword-to-string (get-in world [:murder-case :room]))
        weapon (keyword-to-string (get-in world [:murder-case :weapon]))
        current-room (keyword-to-string (current-room world))]
    (format (first @lose-time) murderer current-room victim room weapon)))

(defn get-murderer-alibi
  "Returns an alibi for the murderer."
  [murderer guests]
  (let [alone (ffirst (filter #(= (:alibi (second %)) :alone) guests))
        guests (dissoc guests murderer)
        guests (dissoc guests alone)
        alibi (rand-nth (keys guests))]
    (format (rand-nth @alibis) (keyword-to-first-name alibi) (keyword-to-string (random-room)))))

(defn get-alibi
  "Returns an alibi string given a guest and an alibi keyword."
  [guest guests]
  (let [alibi (get-in guests [guest :alibi])
        room (get-in guests [guest :alibi-room])]
    (case alibi
      :murderer (get-murderer-alibi guest guests)
      :alone (format (first @alone-alibi) (keyword-to-string room))
      ;default
      (format (rand-nth @alibis) (keyword-to-first-name alibi) (keyword-to-string room)))))

(defn get-alibis
  "Adds an alibi for every guest in guests"
  [guests]
  (loop [guests guests
         guest-names (keys guests)]
    (if (empty? guest-names)
      guests
      (let [guest (first guest-names)
            guests (assoc-in guests [guest :alibi-string] (get-alibi guest guests))]
        (recur guests (rest guest-names))))))

(defn random-first-response
  "Returns a random response for the first time a guest is asked for an alibi."
  [guest alibi-string world]
  (let [victim (keyword-to-first-name (get-in world [:murder-case :victim]))
        alibi (get-in world [:entities :guests guest :alibi])
        opener (format (rand-nth @openers) (keyword-to-first-name guest))
        accuse (rand-int 4)
        accused (keyword-to-first-name (rand-nth
                                        (remove #{victim alibi guest}
                                                (keys (get-in world [:entities :guests])))))]
    (cond
     (= alibi :alone)
      (str opener alibi-string (format (rand-nth @alone-additions) victim))

     (= accuse 0)
       (str opener alibi-string (format (rand-nth @accusations) accused victim))

     :else
       (str opener alibi-string (format (rand-nth @additions) victim)))))

(defn random-response
  "Returns a random response for subsequent times a guest is asked for an alibi."
  [guest alibi-string times]
  (let [opener (format (rand-nth @repeat-openers) (keyword-to-first-name guest))
        repeater (rand-nth @repeats)
        finish (rand-nth @finished)]
    (if (= times 1)
      (str opener repeater  " " alibi-string finish)
      (str opener repeater " " alibi-string "\""))))

(defn create-alibi-message
  "Creates an alibi based on the guest and number of times the guest has been asked."
  [guest times world]
  (let [alibi-string (get-in world [:entities :guests guest :alibi-string])]
    (if (= times 0)
      (random-first-response guest alibi-string world)
      (random-response guest alibi-string times))))