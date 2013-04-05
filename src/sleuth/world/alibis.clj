(ns sleuth.world.alibis
  (:use [sleuth.utils :only [keyword-to-first-name keyword-to-string]]
        [sleuth.world.rooms :only [random-room]])
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

;when staring at floor
;"%s looks up from the floor and says,"

; added after alibi when being asked for second time
;"I can't think of anything else to add."

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
    (deliver refuse (:refuse alibis-map))))

(defn get-murderer-alibi
  "Returns an alibi for the murderer."
  [murderer guests]
  (let [alone (ffirst (filter #(= (:alibi (second %)) :alone) guests))
        guests (dissoc guests murderer)
        guests (dissoc guests alone)
        alibi (rand-nth (keys guests))]
    (println "Murderer!")
    (format (rand-nth @alibis) (keyword-to-first-name alibi) (keyword-to-string (random-room)))))

(defn get-alibi
  "Returns an alibi string given a guest and an alibi keyword."
  [guest guests]
  (let [alibi (get-in guests [guest :alibi])
        room (get-in guests [guest :alibi-room])]
    (println "Alibi: " alibi)
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
    ;(println guests)
    (if (empty? guest-names)
      (do
        (println guests)
        guests)
      (let [guest (first guest-names)
            guests (assoc-in guests [guest :alibi-string] (get-alibi guest guests))]
        (recur guests (rest guest-names))))))

(defn random-first-response
  "Returns a random response for the first time a guest is asked for an alibi."
  [guest alibi-string world]
  (let [victim (get-in world [:murder-case :victim])
        alibi (get-in world [:entities :guests guest :alibi])
        opener (format (rand-nth @openers) (keyword-to-first-name guest))]
    (if (= alibi :alone)
      (str opener alibi-string (format (rand-nth @alone-additions) (keyword-to-first-name victim)))
      alibi-string)))

(defn create-alibi-message
  "Creates an alibi based on the guest and number of times the guest has been asked."
  [guest times world]
  (let [alibi-string (get-in world [:entities :guests guest :alibi-string])]
    (case times
      0 (random-first-response guest alibi-string world)
      alibi-string)))