(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI instructions]]
        [sleuth.world.core :only [new-world]]
        [sleuth.world.rooms :only [get-room-description]]
        [sleuth.entities.player :only [move-player make-player]]
        [sleuth.commands :only [process-command]]
        [sleuth.libtcod])
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
  (let [world (new-world)
        player (make-player world)]
    (-> game
      (assoc :world world)
      (assoc-in [:world :entities :player] player)
      (assoc :uis [(->UI :sleuth)]))))

(defmethod process-input :menu [game input]
  (cond
    (= (.c input) (int \a)) (new-game game) 
    (= (.c input) (int \b)) (assoc game :uis [(->UI :personalize)])
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
  "Does nothing yet -- returns to menu screen."
  (assoc game :uis [(->UI :menu)]))

; Sleuth ------------------------------------------------------------------
(defmethod process-input :sleuth [game input]
  (cond 
    ; return to menu
    (= (.vk input) key-escape) (assoc game :uis [(->UI :menu)]) ; testing

    ; movement keys
    (= (.vk input) key-left) (let [new-game (update-in game [:world] move-player :w)
                new-location (get-in new-game [:world :entities :player :location])
                world (:world new-game)]
            (assoc-in new-game [:world :message] (get-room-description new-location world)))

    (= (.vk input) key-down) (let [new-game (update-in game [:world] move-player :s)
                new-location (get-in new-game [:world :entities :player :location])
                world (:world new-game)]
            (assoc-in new-game [:world :message] (get-room-description new-location world)))
    
    (= (.vk input) key-up) (let [new-game (update-in game [:world] move-player :n)
              new-location (get-in new-game [:world :entities :player :location])
              world (:world new-game)]
            (assoc-in new-game [:world :message] (get-room-description new-location world)))    
    
    (= (.vk input) key-right) (let [new-game (update-in game [:world] move-player :e)
                 new-location (get-in new-game [:world :entities :player :location])
                 world (:world new-game)]
            (assoc-in new-game [:world :message] (get-room-description new-location world)))   

   ; commandline keys 
   (= (.vk input) key-backspace) (let [world (:world game)
                                        {:keys [commandline]} world]
                                    (assoc-in game [:world :commandline]
                                              (subs commandline 0 
                                                    (max (- (count commandline) 1) 0))))
    
    (= (.vk input) key-enter) (let [world (:world game)]
                                (-> game
                                    (assoc-in [:world] (process-command world))
                                    (assoc-in [:world :commandline] "")))
    
    (= (.vk input) key-char) (let [world (:world game)
                                   {:keys [commandline]} world]
                               (assoc-in game [:world :commandline] 
                                         (str commandline (char (.c input)))))
   
    (= (.vk input) key-space) (let [world (:world game)
                                    {:keys [commandline]} world]
                                (assoc-in game [:world :commandline] 
                                          (str commandline " ")))

    :else game))

; Input processing -------------------------------------------------------
(defn get-input
  "Gets user's keypress."
  [game screen]
  (assoc game :input (console-wait-for-keypress true)))
