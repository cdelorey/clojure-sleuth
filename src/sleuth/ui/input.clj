(ns sleuth.ui.input
  (:require [lanterna.screen :as s]))

; Definitions ------------------------------------------------------------
(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

; Start ------------------------------------------------------------------
(defmethod process-input :start [game input]
  (assoc game :uis []))

; Input processing -------------------------------------------------------
(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
