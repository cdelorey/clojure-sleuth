(ns sleuth.world.alibis
  (:use [sleuth.utils :only [keyword-to-first-name keyword-to-string parse-file]]
        [sleuth.world.rooms :only [random-room current-room]])
	(:require [goog.string :as gstring]
						[goog.string.format :as gformat]))

; Alibi components ----------------------------------------------------------------------------

; used at beginning of an alibi the first time a suspect is asked.
(def openers (atom nil))
; used at beginning of an alibi when the guest has already been asked
(def repeat-openers (atom nil))
; Alibis
(def alibis (atom nil))
; alone alibi
(def alone-alibi (atom nil))
; Additional things guests will say when asked the first time.
(def additions (atom nil))
; Additional comments by guest who was alone all night.
(def alone-additions (atom nil))
; Additional things guests will say when asked for the first time
; that include an accusation of another guest.
(def accusations (atom nil))
; said after a repeat opener
(def repeats (atom nil))
; said when a guest refuses to answer questions
(def refuse (atom nil))
; said the second time a guest is asked
(def finished (atom nil))
; text displayed when player loses from questioning murderer too much
(def lose-questioning (atom nil))
; text displayed when player loses from running out of time
(def lose-time (atom nil))

; Alibi Functions -----------------------------------------------------------------------------
(defn load-alibis
  "Loads alibi components from filename"
  [filename]
	(parse-file filename
							(fn [alibis-map]
								(do
									(reset! openers (:openers alibis-map))
								 	(reset! repeat-openers (:repeat-openers alibis-map))
								 	(reset! alibis (:alibis alibis-map))
								 	(reset! alone-alibi (:alone-alibi alibis-map))
								 	(reset! additions (:additions alibis-map))
								 	(reset! alone-additions (:alone-additions alibis-map))
								 	(reset! accusations (:accusations alibis-map))
								 	(reset! repeats (:repeats alibis-map))
								 	(reset! refuse (:refuse alibis-map))
								 	(reset! finished (:finished alibis-map))
								 	(reset! lose-questioning (:lose-questioning alibis-map))
								 	(reset! lose-time (:lose-time alibis-map))))))

(defn get-lose-questioning
  "Returns the lose-questioning text with the proper values filled in"
  [world]
  (let [murderer (keyword-to-first-name (get-in world [:murder-case :murderer]))
        victim (keyword-to-first-name (get-in world [:murder-case :victim]))
        room (keyword-to-string (get-in world [:murder-case :room]))
        weapon (keyword-to-string (get-in world [:murder-case :weapon]))]
    (gstring/format (first @lose-questioning) murderer victim room weapon)))

(defn get-lose-time
  "Returns the lose-time text with the proper values filled in"
  [world]
  (let [murderer (keyword-to-first-name (get-in world [:murder-case :murderer]))
        victim (keyword-to-first-name (get-in world [:murder-case :victim]))
        room (keyword-to-string (get-in world [:murder-case :room]))
        weapon (keyword-to-string (get-in world [:murder-case :weapon]))
        current-room (keyword-to-string (current-room world))]
    (gstring/format (first @lose-time) murderer current-room victim room weapon)))

(defn get-murderer-alibi
  "Returns an alibi for the murderer."
  [murderer guests]
  (let [alone (ffirst (filter #(= (:alibi (second %)) :alone) guests))
        guests (dissoc guests murderer)
        guests (dissoc guests alone)
        alibi (rand-nth (keys guests))]
    (gstring/format (rand-nth @alibis) (keyword-to-first-name alibi) (keyword-to-string (random-room)))))

(defn get-alibi
  "Returns an alibi string given a guest and an alibi keyword."
  [guest guests]
  (let [alibi (get-in guests [guest :alibi])
        room (get-in guests [guest :alibi-room])]
		(.log js/console (rand-nth @alibis))
    (case alibi
      :murderer (get-murderer-alibi guest guests)
      :alone (gstring/format (first @alone-alibi) (keyword-to-string room))
      ;default
      (gstring/format (rand-nth @alibis) (keyword-to-first-name alibi) (keyword-to-string room)))))

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
        opener (gstring/format (rand-nth @openers) (keyword-to-first-name guest))
        accuse (rand-int 4)
        accused (keyword-to-first-name (rand-nth
                                        (remove #{victim alibi guest}
                                                (keys (get-in world [:entities :guests])))))]
    (cond
     (= alibi :alone)
      (str opener alibi-string (gstring/format (rand-nth @alone-additions) victim))

     (= accuse 0)
       (str opener alibi-string (gstring/format (rand-nth @accusations) accused victim))

     :else
       (str opener alibi-string (gstring/format (rand-nth @additions) victim)))))

(defn random-response
  "Returns a random response for subsequent times a guest is asked for an alibi."
  [guest alibi-string times]
  (let [opener (gstring/format (rand-nth @repeat-openers) (keyword-to-first-name guest))
        repeater (rand-nth @repeats)
        finish (rand-nth @finished)]
    (if (= times 1)
      (str opener repeater  " " alibi-string finish)
      (str opener repeater " " alibi-string "\""))))

(defn random-refuse-response
  "Returns a random response for when a guest refuses to be questioned."
  [guest]
  (gstring/format (rand-nth @refuse) (keyword-to-first-name guest)))

(defn refuse-questioning?
  "Returns true if a guest refuses to be questioned, based on the number of
  questions that have already been asked."
  [times]
  (if (>= (rand-int 10) times)
    false
    true))

(defn create-alibi-message
  "Creates an alibi based on the guest and number of times the guest has been asked.

  There is a random chance that the guest will refuse to be questioned. The likelihood
  increases with the number of questions the guest has been asked."
  [guest times world]
  (let [alibi-string (get-in world [:entities :guests guest :alibi-string])]
    (if (= times 0)
      (random-first-response guest alibi-string world)
      (if (refuse-questioning? times)
        (random-refuse-response guest)
        (random-response guest alibi-string times)))))
