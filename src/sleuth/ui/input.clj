(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI]])
  (:require [lanterna.screen :as s]))

; Definitions ------------------------------------------------------------
(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

; Start ------------------------------------------------------------------
(defmethod process-input :start [game input]
  (assoc game :uis [(->UI :menu)]))

; Menu ------------------------------------------------------------------
(defmethod process-input :menu [game input]
  (case input
    (\a \A) (assoc game :uis [(->UI :sleuth)])
    (\b \B) (assoc game :uis [(->UI :personalize)])
    (\c \C) (assoc game :uis [(->UI :instructions)])
    (\q \Q) (assoc game :uis [])
    game))

; Instructions ------------------------------------------------------------
(defmethod process-input :instructions [game input]
  "Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))

; Personalize -------------------------------------------------------------
(defmethod process-input :personalize [game input]
  "Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))

; Sleuth ------------------------------------------------------------------
(defmethod process-input :sleuth [game input]
  "Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))


; Input processing -------------------------------------------------------
(defn get-input [game screen]
  "Gets user's keypress."
  (assoc game :input (s/get-key-blocking screen)))
