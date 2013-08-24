(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI instructions]]
        [sleuth.world.core :only [new-world]]
        [sleuth.world.rooms :only [get-room-description]]
        [sleuth.entities.player :only [move-player make-player]]
        [sleuth.commands :only [process-command process-game-over-commands
                                process-accuse-commands]]
        [sleuth.personalize :only [new-personalize process-personalize-input]]
        [sleuth.libtcod]))

; Definitions ------------------------------------------------------------
(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

; Start ------------------------------------------------------------------
(defmethod process-input :start [game input]
  (assoc game :uis [(->UI :menu)]))

; Menu ------------------------------------------------------------------
(defn new-game [game]
  (let [world (new-world)
        player (make-player world)]
    (-> game
      (assoc :world world)
      (assoc-in [:world :entities :player] player)
      (assoc :uis [(->UI :sleuth)]))))

(defmethod process-input :menu [game input]
  (cond
    (= (.c input) (int \a)) (new-game game)
    (= (.c input) (int \b)) (-> game
                                (assoc-in [:uis] [(->UI :personalize)])
                                (assoc-in [:personalize] (new-personalize)))
    (= (.c input) (int \c)) (assoc (assoc-in
                     game [:instructions] instructions)
                 :uis [(->UI :instructions)])
    (= (.c input) (int \q)) (assoc game :uis [])
    :else game))

; Instructions ------------------------------------------------------------
(defmethod process-input :instructions [game input]
  "Cycle through instructions with each keypress.
  Return to main menu when instructions are empty."
  (if (empty? (next (game :instructions)))
    (assoc game :uis [(->UI :menu)])
    (assoc game :instructions (rest (game :instructions)))))

; Personalize -------------------------------------------------------------
(defmethod process-input :personalize [game input]
  (let [current-box (:current-box (:personalize game))
        input-box (current-box (:gui (:personalize game)))
        data (:data input-box)]
  (cond
   (= (.vk input) key-backspace) (assoc-in game [:personalize :gui current-box :data]
                                              (subs data 0 (max (- (count data) 1) 0)))

   (= (.vk input) key-enter) (process-personalize-input game)

   (= (.vk input) key-char) (assoc-in game [:personalize :gui current-box :data]
                                         (str data (char (.c input))))

   (= (.vk input) key-space) (assoc-in game [:personalize :gui current-box :data]
                                          (str data " "))
   :else game)))


; Sleuth ------------------------------------------------------------------
(defn move
  "Move player in specified direction."
  [direction game]
  (let [new-game (update-in game [:world] move-player direction)
        new-location (get-in new-game [:world :entities :player :location])
        world (:world new-game)]
    (assoc-in new-game [:world :message] (get-room-description new-location world))))

(defn process-movement-keys
  "Process player movement.

  Takes a move function so different uis can process movement differently."
  [game input move-function]
  (cond
   (= (.vk input) key-left) (move-function :w game)
   (= (.vk input) key-down) (move-function :s game)
   (= (.vk input) key-up) (move-function :n game)
   (= (.vk input) key-right) (move-function :e game)
   :else game))

(defn process-commandline-input
  [game input command-function]
  "Process commandline input.

  Takes a command function so different uis can process commands differently."
  (cond
   (= (.vk input) key-backspace) (let [world (:world game)
                                        {:keys [commandline]} world]
                                    (assoc-in game [:world :commandline]
                                              (subs commandline 0
                                                    (max (- (count commandline) 1) 0))))

   (= (.vk input) key-enter) (let [new-game (command-function game)]
                                (assoc-in new-game [:world :commandline] ""))

   (= (.vk input) key-char) (let [world (:world game)
                                   {:keys [commandline]} world]
                               (assoc-in game [:world :commandline]
                                         (str commandline (char (.c input)))))

   (= (.vk input) key-space) (let [world (:world game)
                                    {:keys [commandline]} world]
                                (assoc-in game [:world :commandline]
                                          (str commandline " ")))
   :else game))

(defmethod process-input :sleuth [game input]
  (cond
   ; return to menu
   (= (.vk input) key-escape) (assoc game :uis [(->UI :menu)]) ; testing

   ; commandline keys
   (contains? #{key-backspace key-enter key-char key-space} (.vk input))
   (process-commandline-input game input process-command)

   :else (process-movement-keys game input move)))

; Assemble ----------------------------------------------------------------
(defn assemble-move
  "Move player in specified direction without displaying room descriptions."
  [direction game]
  (update-in game [:world] move-player direction))


(defmethod process-input :assemble [game input]
    (cond
     (contains? #{key-backspace key-enter key-char key-space} (.vk input))
     (process-commandline-input game input process-accuse-commands)

     :else (process-movement-keys game input assemble-move)))

; Game Over ---------------------------------------------------------------
(defmethod process-input :game-over [game input]
  (let [game (assoc-in game [:world :message] "Quit or Restart?")]
    (cond
     (contains? #{key-backspace key-enter key-char key-space} (.vk input))
     (process-commandline-input game input process-game-over-commands)

     :else game)))

; Input processing -------------------------------------------------------
(defn get-input
  "Gets user's keypress."
  [game screen]
  (assoc game :input (console-wait-for-keypress true)))
