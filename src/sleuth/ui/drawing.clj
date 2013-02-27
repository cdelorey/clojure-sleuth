(ns sleuth.ui.drawing
  (:use [sleuth.utils :only [map2d]])
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
  (s/put-sheet screen 5 0 (clojure.string/split 
                            (first (game :instructions)) #"\newline")))

; Personalize -------------------------------------------------------------
(defmethod draw-ui :personalize [ui game screen]
  (s/put-string screen 10 10 "Press any key to return to menu."))

; Sleuth ------------------------------------------------------------------  
(defn draw-house [screen tiles]
  (doseq [y (range 0 (count tiles))
          :let [rowtiles (tiles y)]]
    (doseq [x (range 0 (count (tiles 0)))
            :let [{:keys [glyph color]} (rowtiles x)]]
      (s/put-string screen x y glyph {:fg color}))))

(defn draw-message [screen message]
  (s/put-sheet screen 0 19 (clojure.string/split message #"\n")))

(defn draw-commandline [screen commandline]
  (s/put-string screen 0 24 (str ">" commandline) {:fg :white})
  (s/move-cursor screen (+ (count commandline) 1) 24))

(defn draw-player [screen player]
  (let [[x y] (:location player)]
    (s/put-string screen x y (:glyph player) {:fg :white})))

(defmethod draw-ui :sleuth [ui game screen]
  (let [world (:world game)
        {:keys [tiles message commandline entities]} world
        player (:player entities)]
    (draw-house screen tiles)
    (draw-message screen message)
    (draw-commandline screen commandline)
    (draw-player screen player)))


; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (s/clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen)
  game)
