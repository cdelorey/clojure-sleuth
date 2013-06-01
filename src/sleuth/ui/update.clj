(ns sleuth.ui.update
  (:use [sleuth.ui.input :only [lose-game]]))

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
     (= turn-count 200)
     (assoc-in new-world [:flags :murderer-is-suspicious] true)

     (= turn-count 300)
     (assoc-in new-world [:flags :murderer-is-stalking] true)

    :else new-world)))

(defmethod update :sleuth
  [game]
  "Checks if game has been won or lost and updates turn count."
  (let [game-lost (get-in game [:world :flags :game-lost])]
    (if game-lost
      (lose-game game)
      (assoc-in game [:world] (new-turn (:world game))))))


; Lose Game --------------------------------------------------------------
(defmethod update :lose-game [game]
  "Does nothing."
  game)