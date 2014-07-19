(ns sleuth.state.update
  (:use [sleuth.state.core :only [->State]]
        [sleuth.world.rooms :only [lock-current-room current-room]]
        [sleuth.entities.guests :only [move-guests move-murderer]]
        [sleuth.utils :only [keyword-to-string]]))


; Definitions ------------------------------------------------------------
(defmulti update
  (fn [game]
    (:name (last (:states game)))))

; Sleuth -----------------------------------------------------------------
(defn new-turn
	"Updates turn count and checks for events."
	[world input]
	(if (contains? #{js/ROT.VK_UP js/ROT.VK_DOWN js/ROT.VK_LEFT
									 js/ROT.VK_RIGHT js/ROT.VK_RETURN} input)
		(let [new-world (update-in world [:murder-case :turn-count] inc)
					turn-count (get-in new-world [:murder-case :turn-count])]
			(cond
			 (= turn-count 200)
			 (assoc-in new-world [:flags :murderer-is-suspicious] true)

			 (= turn-count 300)
			 (assoc-in new-world [:flags :murderer-is-stalking] true)

			 :else new-world))
		world))


(defn assemble-guests
  "Switch to assemble state"
  [game]
  (let [current-room (keyword-to-string (current-room (:world game)))
        assemble-text (str "The suspects have all gathered here in the " current-room
                           " to hear your accusation. The door is now locked.")]
    (as-> game game
        (assoc-in game [:world] (move-guests (:world game)))
        (assoc-in game [:world] (lock-current-room (:world game)))
        (assoc-in game [:states] [(->State :assemble)])
        (assoc-in game [:world :message] assemble-text))))

(defn lose-game
  "Lose game and switch to game-over state."
  [game]
  (let [lose-text (get-in game [:world :murder-case :lose-text])]
    (as-> game game
        (assoc-in game [:world] (move-murderer (:world game)))
        (assoc-in game [:world] (lock-current-room (:world game)))
        (assoc-in game [:states] [(->State :game-over)])
        (assoc-in game [:world :message] lose-text))))

(defmethod update :sleuth
  [game]
  (let [game-lost (get-in game [:world :flags :game-lost])
        assemble  (get-in game [:world :flags :assemble])]
    ; check flags
    (cond
     game-lost    (lose-game game)
     assemble     (assemble-guests game)
     ; update
     :else        (assoc-in game [:world] (new-turn (:world game) (:input game))))))

; Assemble --------------------------------------------------------------
(defn game-over
  "Switch to game-over state after an accusation."
  [game]
  (let [turn-count (get-in game [:world :murder-case :turn-count])]
    (-> game
        (assoc-in [:world :message] (str "The game is over after " turn-count " turns"))
        (assoc-in [:states] [(->State :game-over)]))))


(defmethod update :assemble [game]
  (cond
   (get-in game [:world :flags :game-over]) (game-over game)
   (get-in game [:world :flags :accused]) (assoc-in game[:world :flags :game-over] true)
   :else game))


; Default ---------------------------------------------------------------------
(defmethod update :default [game]
  "Does nothing."
  game)
