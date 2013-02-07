(ns sleuth.ui.drawing
  (:require [lanterna.screen :as s]))

; Definitions -------------------------------------------------------------
(def screen-cols 80)
(def screen-rows 25)

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

; Start -------------------------------------------------------------------
(defmethod draw-ui :start [ui game screen]
  "TODO: read sleuth start screen from file."
  (s/put-string screen (- (/ screen-cols 2) 5) 5 "S L E U T H" {:fg :magenta})
  (s/put-string screen (- (/ screen-cols 2) 6) 8 "Press any key." {:fg :white}))

; Menu --------------------------------------------------------------------
(defmethod draw-ui :menu [ui game screen]
  (s/put-string screen (- (/ screen-cols 2) 5) 5 "S L E U T H" {:fg :blue})
  (s/put-string screen (- (/ screen-cols 2) 16) 10 "Select the option of your choice:")
  (s/put-string screen (- (/ screen-cols 2) 10) 12 "(A) Basic Sleuth")
  (s/put-string screen (- (/ screen-cols 2) 10) 13 "(B) Personalized Sleuth")
  (s/put-string screen (- (/ screen-cols 2) 10) 14 "(C) Review Instructions")
  (s/put-string screen (- (/ screen-cols 2) 13) 19 
                "PRESS Q TO EXIT THIS PROGRAM" {:fg :black :bg :white}))

; Instructions ------------------------------------------------------------
(defmethod draw-ui :instructions [ui game screen]
  (s/put-string screen 10 10 "Instructions:")
  (s/put-string screen 10 11 "Press any key to return to menu."))

; Personalize -------------------------------------------------------------
(defmethod draw-ui :personalize [ui game screen]
  (s/put-string screen 10 10 "Press any key to return to menu."))

; Sleuth ------------------------------------------------------------------
(defmethod draw-ui :sleuth [ui game screen]
  (s/put-string screen 10 10 "Press any key to return to menu."))

; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (s/clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen)
  game)
