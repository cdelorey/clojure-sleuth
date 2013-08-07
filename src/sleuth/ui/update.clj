(ns sleuth.ui.update
  (:use [sleuth.ui.core :only [->UI]]
        [sleuth.world.rooms :only [lock-current-room current-room]]
        [sleuth.entities.guests :only [move-guests]]
        [sleuth.utils :only [keyword-to-string]]))

; Definitions ------------------------------------------------------------
(defmulti update
  (fn [game]
    (:kind (last (:uis game)))))

; Start ------------------------------------------------------------------
(defmethod update :start [game]
  "Does nothing yet. Will use this to animate sleuth letters in start screen."
  game)


; Menu -------------------------------------------------------------------
(defmethod update :menu [game]
  "Does nothing."
  game)


; Instructions -----------------------------------------------------------
(defmethod update :instructions [game]
  "Does nothing."
  game)


; Personalize ------------------------------------------------------------
(defmethod update :personalize [game]
  "Does nothing."
  game)


; Sleuth -----------------------------------------------------------------
(defn new-turn
  "Updates turn count and checks for turn-count dependent events."
  [world]
  (let [new-world (update-in world [:murder-case :turn-count] inc)
        turn-count (get-in new-world [:murder-case :turn-count])]
    (cond
     (= turn-count 150)
     (assoc-in new-world [:flags :guests-stare-at-floor] true)

     (= turn-count 200)
     (assoc-in new-world [:flags :murderer-is-suspicious] true)

     (= turn-count 300)
     (assoc-in new-world [:flags :murderer-is-stalking] true)

     ;testing
     (= turn-count 3)
     (assoc-in new-world [:flags :found-murder-weapon] true)

    :else new-world)))

; TODO: accuse-guests and lose-game are basically the same. refactor.
(defn assemble-guests
  "Switch to assemble ui"
  [game]
  (let [current-room (keyword-to-string (current-room (:world game)))
        assemble-text (str "The suspects have all gathered here in the " current-room
                           " to hear your accusation. The door is now locked.")]
    (as-> game game
        (assoc-in game [:world] (move-guests (:world game)))
        (assoc-in game [:world] (lock-current-room (:world game)))
        (assoc-in game [:uis] [(->UI :assemble)])
        (assoc-in game [:world :message] assemble-text))))

(defn lose-game
  "Switch to lose-game ui."
  [game]
  (let [lose-text (get-in game [:world :murder-case :lose-text])]
    (-> game
        (assoc-in [:uis] [(->UI :lose-game)])
        (assoc-in [:world :message] lose-text))))

(defmethod update :sleuth
  [game]
  (let [game-lost (get-in game [:world :flags :game-lost])
        assemble (get-in game [:world :flags :assemble])]
    (cond
     game-lost  (lose-game game)
     assemble     (assemble-guests game)
     :else        (assoc-in game [:world] (new-turn (:world game))))))

; Assemble --------------------------------------------------------------
(defmethod update :assemble [game]
  "Does nothing."
  game)


; Lose Game --------------------------------------------------------------
(defmethod update :lose-game [game]
  "Does nothing."
  game)