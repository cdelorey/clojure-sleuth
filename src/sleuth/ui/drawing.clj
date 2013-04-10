(ns sleuth.ui.drawing
  (:use [sleuth.utils :only [map2d]]
        [sleuth.libtcod]))

; Definitions -------------------------------------------------------------
(def screen-cols 80)
(def screen-rows 25)

(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

; Start -------------------------------------------------------------------
(defmethod draw-ui :start [ui game screen]
  "TODO: read sleuth start screen from file."
  (console-print screen (- (/ screen-cols 2) 5) 5 "S L E U T H")
  (console-print screen (- (/ screen-cols 2) 6) 8 "Press any key."))

; Menu --------------------------------------------------------------------
(defmethod draw-ui :menu [ui game screen]
  (console-print screen (- (/ screen-cols 2) 5) 5 "S L E U T H")
  (console-print screen (- (/ screen-cols 2) 16) 10 "Select the option of your choice:")
  (console-print screen (- (/ screen-cols 2) 10) 12 "(A) Basic Sleuth")
  (console-print screen (- (/ screen-cols 2) 10) 13 "(B) Personalized Sleuth")
  (console-print screen (- (/ screen-cols 2) 10) 14 "(C) Review Instructions")
  (console-print screen (- (/ screen-cols 2) 13) 19 "PRESS Q TO EXIT THIS PROGRAM" ))

; Instructions ------------------------------------------------------------
(defmethod draw-ui :instructions [ui game screen]
  (console-print screen 5 0 (first (game :instructions))))

; Personalize -------------------------------------------------------------
(defmethod draw-ui :personalize [ui game screen]
  (console-print screen 10 10 "Press any key to return to menu."))

; Sleuth ------------------------------------------------------------------  
(defn draw-house [screen tiles]
  (doseq [y (range 0 (count tiles))
          :let [rowtiles (tiles y)]]
    (doseq [x (range 0 (count (tiles 0)))
            :let [{:keys [glyph color]} (rowtiles x)]]
      (console-set-char screen x y (int glyph)))))

(defn draw-message [screen message]
  (console-print-rect screen 0 19 screen-cols screen-rows message))

(defn draw-commandline [screen commandline]
  (console-print screen 0 24 (str ">" commandline))
  (console-set-char screen (+ (count commandline) 1) 24 95))

(defn draw-player [screen player]
  (let [[x y] (:location player)]
    (console-set-char screen x y char-smilie)));(:glyph player))))

(defn draw-guests [screen guests]
  (doall (map 
          #(console-set-char screen (first (:location %)) (second (:location %))char-smilie) (vals guests))))

(defn draw-sleuth 
  [game screen]
  (let [world (:world game)
        {:keys [tiles message commandline entities]} world
        player (:player entities)
        guests (:guests entities)]
    (draw-house screen tiles)
    (draw-message screen message)
    (draw-commandline screen commandline)
    (draw-player screen player)
    (draw-guests screen guests)))

(defmethod draw-ui :sleuth [ui game screen]
  (draw-sleuth game screen))

; Lose Game ---------------------------------------------------------------
(defmethod draw-ui :lose-game [ui game screen]
  (draw-sleuth game screen))

; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (console-clear screen) 
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (console-flush)
  game)
