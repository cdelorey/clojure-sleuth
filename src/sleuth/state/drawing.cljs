(ns sleuth.state.drawing
  (:use [sleuth.world.rooms :only [get-current-guest]]))

; Definitions -------------------------------------------------------------
(def screen-cols 80)
(def screen-rows 25)

(def char-smilie "☻")

(defmulti draw-state
  (fn [state game screen]
    (:name state)))

; Start -------------------------------------------------------------------
(defmethod draw-state :start [state game screen]
  "TODO: read sleuth start screen from file."
 (.drawText screen (- (/ screen-cols 2) 5) 5 "S L E U T H")
 (.drawText screen (- (/ screen-cols 2) 6) 8 "Press any key."))

; Menu --------------------------------------------------------------------
(defmethod draw-state :menu [state game screen]
  (.drawText screen (- (/ screen-cols 2) 5) 5 "S L E U T H")
  (.drawText screen (- (/ screen-cols 2) 16) 10 "Select the option of your choice:")
  (.drawText screen (- (/ screen-cols 2) 10) 12 "(A) Basic Sleuth")
  (.drawText screen (- (/ screen-cols 2) 10) 13 "(B) Personalized Sleuth")
  (.drawText screen (- (/ screen-cols 2) 10) 14 "(C) Review Instructions")
  (.drawText screen (- (/ screen-cols 2) 13) 19 "PRESS Q TO EXIT THIS PROGRAM" ))

; Instructions ------------------------------------------------------------
(defmethod draw-state :instructions [state game screen]
  (do
    (.drawText screen 35 5 "INSTRUCTIONS")
    (.drawText screen 10 9 (:instructions game) 60)))

; Personalize -------------------------------------------------------------
(defn draw-boxes [screen gui]
  (doall
   (map
    #(.drawText screen (:x %) (:y %) (:data %)) (vals gui))))

(defn draw-cursor [screen personalize]
  (let [current-box ((:current-box personalize) (:gui personalize))
        data (:data current-box)
        x (+ (:x current-box) (count data))
        y (:y current-box)]
 	    (.draw screen x y "_")))

(defmethod draw-state :personalize [state game screen]
  (let [suspect-number (:suspect-number (:personalize game))]
    (.drawText screen 20 1 "P E R S O N A L I Z E   S L E U T H")
    (.drawText screen 10 8 (str "Suspect #" suspect-number))
    (.drawText screen 10 10 "Enter first name: ")
    (.drawText screen 10 12 "Enter second name: ")
    (draw-boxes screen (:gui (:personalize game)))
    (draw-cursor screen (:personalize game))))

; Opening -----------------------------------------------------------------
(defmethod draw-state :opening [state game screen]
  (.drawText screen 10 10 (str "It is a dark and stormy night."
                                   " A murder is being committed ...")))

; Sleuth ------------------------------------------------------------------
(defn draw-house [screen tiles]
  (doseq [y (range 0 (count tiles))
          :let [rowtiles (tiles y)]]
    (doseq [x (range 0 (count (tiles 0)))
            :let [{:keys [glyph color]} (rowtiles x)]]
      (.draw screen x y glyph))))

(defn draw-message [screen message]
  (.drawText screen 0 19 message screen-cols))

(defn draw-commandline [screen commandline]
  (.drawText screen 0 24 (str ">" commandline))
  (.draw screen (+ (count commandline) 1) 24 "_"))

(defn draw-player [screen player]
  (let [[x y] (:location player)]
    (.draw screen x y char-smilie)))

(defn draw-guests [screen guests]
  (doall (map
          #(.draw screen (first (:location %)) (second (:location %))char-smilie) (vals guests))))

(defn draw-guest
  "Draws the guest in the player's current room, if there is one."
  [screen world]
  (let [guest (get-current-guest world)]
    (if guest
      (let [[x y] (get-in world [:entities :guests guest :location])]
        (.draw screen x y char-smilie)))))

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

(defmethod draw-state :sleuth [state game screen]
  (draw-sleuth game screen))

; Assemble ----------------------------------------------------------------
(defmethod draw-state :assemble [state game screen]
  (draw-sleuth game screen))

; Game Over ---------------------------------------------------------------
(defmethod draw-state :game-over [state game screen]
  (draw-sleuth game screen))

; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (.clear screen)
  (doseq [state (:states game)]
    (draw-state state game screen))
  ;(console-flush)
  game)
