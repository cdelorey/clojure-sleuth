(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI instructions]]
        [sleuth.world :only [->World load-house]]
        [sleuth.entities.player :only [make-player]])
  (:require [lanterna.screen :as s]))

; Definitions ------------------------------------------------------------
(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

; Start ------------------------------------------------------------------
(defmethod process-input :start [game input]
  (assoc game :uis [(->UI :menu)]))

; Menu ------------------------------------------------------------------
(defn new-game [game]
  (let [new-house (rand-nth
                    ["resources/house22.txt" "resources/house22.txt"])
        new-world (->World (load-house new-house) "oh hi" "what?" {})]
    (-> game
        (assoc :world new-world)
        (assoc-in [:world :entities :player] (make-player new-world))
        (assoc :uis [(->UI :sleuth)]))))

(defmethod process-input :menu [game input]
  (case input
    (\a \A) (new-game game) 
    (\b \B) (assoc game :uis [(->UI :personalize)])
    (\c \C) (assoc (assoc-in
                     game [:instructions] instructions)
                   :uis [(->UI :instructions)])
    (\q \Q) (assoc game :uis [])
    game))

; Instructions ------------------------------------------------------------
(defmethod process-input :instructions [game input]
  "Cycle through instructions with each keypress.
  Return to main menu when instructions are empty."
  (if (empty? (next (game :instructions)))
    (assoc game :uis [(->UI :menu)])
    (assoc game :instructions (rest (game :instructions)))))

; Personalize -------------------------------------------------------------
(defmethod process-input :personalize [game input]
  "Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))

; Sleuth ------------------------------------------------------------------
(defmethod process-input :sleuth [game input]
  ;"Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))

; Input processing -------------------------------------------------------
(defn get-input [game screen]
  "Gets user's keypress."
  (assoc game :input (s/get-key-blocking screen)))
