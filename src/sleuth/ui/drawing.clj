(ns sleuth.ui.drawing
  (:use [sleuth.utils :only [map2d]]
        [sleuth.world.rooms :only [get-current-guest]]
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
(defn draw-boxes [screen gui]
  (doall
   (map
    #(console-print screen (:x %) (:y %) (:data %)) (vals gui))))

(defn draw-cursor [screen personalize]
  (let [current-box ((:current-box personalize) (:gui personalize))
        data (:data current-box)
        x (+ (:x current-box) (count data))
        y (:y current-box)]
      (console-set-char screen x y 95)))       

(defmethod draw-ui :personalize [ui game screen]
  (let [suspect-number (:suspect-number (:personalize game))]
    (console-print screen 20 1 "P E R S O N A L I Z E   S L E U T H")
    (console-print screen 10 8 (str "Suspect #" suspect-number))
    (console-print screen 10 10 "Enter first name: ")
    (console-print screen 10 12 "Enter second name: ")
    (draw-boxes screen (:gui (:personalize game)))
    (draw-cursor screen (:personalize game))))

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

(defn draw-guest
  "Draws the guest in the player's current room, if there is one."
  [screen world]
  (let [guest (get-current-guest world)]
    (if guest
      (let [[x y] (get-in world [:entities :guests guest :location])]
        (console-set-char screen x y char-smilie)))))

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
    (draw-guest screen world)))

(defmethod draw-ui :sleuth [ui game screen]
  (draw-sleuth game screen))

; Assemble ----------------------------------------------------------------
(defmethod draw-ui :assemble [ui game screen]
  (draw-sleuth game screen))

; Game Over ---------------------------------------------------------------
(defmethod draw-ui :game-over [ui game screen]
  (draw-sleuth game screen))

; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (console-clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (console-flush)
  game)
