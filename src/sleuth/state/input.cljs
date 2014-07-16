(ns sleuth.state.input
  (:use [sleuth.state.core :only [->State instructions]]
        [sleuth.world.core :only [new-world]]
        [sleuth.world.rooms :only [get-room-description]]
        [sleuth.world.text :only [random-opening]]
        [sleuth.entities.player :only [move-player make-player]]
        [sleuth.entities.guests :only [personalized-names]]
        [sleuth.commands :only [process-command process-game-over-commands
                                process-accuse-commands]]
        [sleuth.personalize :only [new-personalize swap-current-box]]
        [sleuth.utils :only [keywordize is-char-key? char-keys]])
	(:require [clojure.set :refer [union]]))

; Definitions ------------------------------------------------------------
(def input-keycode-queue (js/Array.))

(defmulti process-input
  (fn [game input]
    (:name (last (:states game)))))

; Start ------------------------------------------------------------------
(defmethod process-input :start [game input]
	(.log js/console "processing input")
  (assoc game :states [(->State :menu)]))

; Menu ------------------------------------------------------------------
(defn new-game [game personalized?]
  (let [world (new-world personalized?)
        player (make-player world)]
    (-> game
      (assoc :world world)
      (assoc-in [:world :entities :player] player)
      (assoc-in [:world :message] (random-opening world))
      (assoc :states [(->State :opening)]))))

(defmethod process-input :menu [game input]
	(cond
	 (= input js/ROT.VK_A) (new-game game false)
	 (= input js/ROT.VK_B) (-> game
														 (assoc-in [:states] [(->State :personalize)])
														 (assoc-in [:personalize] (new-personalize)))
	 (= input js/ROT.VK_C) (assoc (assoc-in
																 game [:instructions] instructions)
													 :states [(->State :instructions)])
	 (= input js/ROT.VK_Q) (assoc game :states [])
	 :else game))

; Instructions ------------------------------------------------------------
(defmethod process-input :instructions [game input]
  "Cycle through instructions with each keypress.
  Return to main menu when instructions are empty."
  (if (empty? (next (game :instructions)))
    (assoc game :states [(->State :menu)])
    (assoc game :instructions (rest (game :instructions)))))

; Personalize -------------------------------------------------------------
(defn switch-to-opening-state
  [game]
  (reset! personalized-names (:name-list (:personalize game)))
  (as-> game game
        (dissoc game :personalize)
        (new-game game true)))

(defn process-personalize-input
  "Process names entered in personalize gui."
  [game]
  (let [current-box (:current-box (:personalize game))]
    (if (= current-box :box-one)
      (assoc-in game [:personalize :current-box] (swap-current-box current-box))
      (let [first-name (get-in game [:personalize :gui :box-one :data])
            last-name (get-in game [:personalize :gui :box-two :data])
            full-name (keywordize (str first-name " " last-name))
            name-list (:name-list (:personalize game))
            game         (-> game
                             (assoc-in [:personalize :name-list] (conj name-list full-name))
                             (assoc-in [:personalize :current-box] (swap-current-box current-box))
                             (assoc-in [:personalize :gui :box-one :data] "")
                             (assoc-in [:personalize :gui :box-two :data] "")
                             (assoc-in [:personalize :suspect-number] (inc (:suspect-number (:personalize game)))))]
        (if (= (:suspect-number (:personalize game)) 8)
          (switch-to-opening-state game)
          game)))))

(defmethod process-input :personalize [game input]
	(let [current-box (:current-box (:personalize game))
				input-box (current-box (:gui (:personalize game)))
				data (:data input-box)]
		(cond
		 (= input js/ROT.VK_BACK_SPACE) (assoc-in game [:personalize :gui current-box :data]
																							(subs data 0 (max (- (count data) 1) 0)))

		 (= input js/ROT.VK_ENTER) (process-personalize-input game)

		 (is-char-key? input) (assoc-in game [:personalize :gui current-box :data]
																	(str data (char (.c input))))

		 (= input js/ROT.VK_SPACE) (assoc-in game [:personalize :gui current-box :data]
																				 (str data " "))

		 (= input js/ROT.VK_ESCAPE) (-> game
																		(dissoc :personalize)
																		(assoc :states [(->State :menu)]))

		 :else game)))


; Opening -----------------------------------------------------------------
(defmethod process-input :opening [game input]
  (assoc game :states [(->State :sleuth)]))

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

  Takes a move function so different states can process movement differently."
  [game input move-function]
  (cond
   (= input js/ROT.VK_LEFT) (move-function :w game)
   (= input js/ROT.VK_DOWN) (move-function :s game)
   (= input js/ROT.VK_UP) (move-function :n game)
   (= input js/ROT.VK_RIGHT) (move-function :e game)
   :else game))

(defn process-commandline-input
	[game input command-function]
	"Process commandline input.

	Takes a command function so different states can process commands differently."
	(cond
	 (= input js/ROT.VK_BACK_SPACE) (let [world (:world game)
																				{:keys [commandline]} world]
																		(assoc-in game [:world :commandline]
																							(subs commandline 0
																										(max (- (count commandline) 1) 0))))

	 (= input js/ROT.VK_ENTER) (let [new-game (command-function game)]
															 (assoc-in new-game [:world :commandline] ""))

	 (= is-char-key? input) (let [world (:world game)
																{:keys [commandline]} world]
														(assoc-in game [:world :commandline]
																			(str commandline (char (.c input)))))

	 (= input js/ROT.VK_SPACE) (let [world (:world game)
																	 {:keys [commandline]} world]
															 (assoc-in game [:world :commandline]
																				 (str commandline " ")))
	 :else game))

(defmethod process-input :sleuth [game input]
(cond
 ;return to menu
 (= input js/ROT.VK_ESCAPE) (assoc game :states [(->State :menu)]) ; testing

 ;commandline keys
 (contains? (union #{js/ROT.VK_BACK_SPACE js/ROT.VK_ENTER js/ROT.VK_SPACE} char-keys) input)
 (process-commandline-input game input process-command)

 :else (process-movement-keys game input move)))

; Assemble ----------------------------------------------------------------
(defn assemble-move
  "Move player in specified direction without displaying room descriptions."
  [direction game]
  (update-in game [:world] move-player direction))


(defmethod process-input :assemble [game input]
    (cond
     (contains? (union #{js/ROT.VK_BACK_SPACE js/ROT.VK_ENTER js/ROT.VK_SPACE} char-keys) input)
     (process-commandline-input game input process-accuse-commands)

     :else (process-movement-keys game input assemble-move)))

; Game Over ---------------------------------------------------------------
(defmethod process-input :game-over [game input]
  (let [game (assoc-in game [:world :message] "Quit or Restart?")]
    (cond
     (contains? (union #{js/ROT.VK_BACK_SPACE js/ROT.VK_ENTER js/ROT.VK_SPACE} char-keys) input)
     (process-commandline-input game input process-game-over-commands)

     :else game)))

; Input processing ------------------------------------------------------
(defn add-keycode-to-queue
	[keycode]
	(.unshift input-keycode-queue keycode))

(defn get-keycode-from-queue
	[]
	(if (< (.-length input-keycode-queue) 1)
		nil
		(.pop input-keycode-queue)))

(defn key-listener
	[event]
	(.log js/console event)
	(if (= (.-type event) "keydown") ; is this necessary?
		(add-keycode-to-queue (.-keyCode event))))

(defn get-input
  "Gets user's keypress."
  [game screen]
	(let [keycode (get-keycode-from-queue)]
		(if keycode
			(assoc game :input keycode)
			game)))
