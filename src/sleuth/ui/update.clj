(ns sleuth.ui.update)

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

(defn lose-game
  "Switch to lose-game ui."
  [game]
  (let [lose-text (get-in game [:world :murder-case :lose-text])]
    (-> game
        (assoc-in [:uis] [(->UI :lose-game)])
        (assoc-in [:world :message] lose-text))))

(defmethod update :sleuth
  [game]
  (let [game-lost (get-in game [:world :flags :game-lost])]
    (if game-lost
      (lose-game game)
      (assoc-in game [:world] (new-turn (:world game))))))


; Lose Game --------------------------------------------------------------
(defmethod update :lose-game [game]
  "Does nothing."
  game)