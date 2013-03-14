(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI instructions]]
        [sleuth.world.core :only [new-world]]
        [sleuth.world.rooms :only [get-room-description]]
        [sleuth.entities.player :only [move-player make-player]]
        [sleuth.commands :only [process-command]])
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
  (case input
    (\a \A) (new-game game) 
    (\b \B) (assoc game :uis [(->UI :personalize)])
    (\c \C) (assoc (assoc-in
                     game [:instructions] instructions)
                   :uis [(->UI :instructions)])
    (\q \Q) (assoc game :uis [])
    game))

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
  (case input
    :escape (assoc game :uis [(->UI :menu)]) ; testing

:left (let [new-game (update-in game [:world] move-player :w)
            new-location (get-in new-game [:world :entities :player :location])]
            (assoc-in new-game [:world :commandline] new-location) ;testing
            (assoc-in new-game [:world :message] (get-room-description new-location)))

    :down (let [new-game (update-in game [:world] move-player :s)
                new-location (get-in new-game [:world :entities :player :location])]
            (println new-location) ;testing
            (assoc-in new-game [:world :message] (get-room-description new-location)))
    
    :up (let [new-game (update-in game [:world] move-player :n)
              new-location (get-in new-game [:world :entities :player :location])]
            (assoc-in new-game [:world :commandline] new-location) ;testing
            (assoc-in new-game [:world :message] (get-room-description new-location)))    
    
    :right (let [new-game (update-in game [:world] move-player :e)
                 new-location (get-in new-game [:world :entities :player :location])]
            (assoc-in new-game [:world :commandline] new-location) ;testing
            (assoc-in new-game [:world :message] (get-room-description new-location)))   

    :backspace (let [world (:world game)
                     {:keys [commandline]} world]
                 (assoc-in game [:world :commandline]
                           (subs commandline 0 
                                 (max (- (count commandline) 1) 0))))
    
    :enter (let [world (:world game)]
             (-> game
               (assoc-in [:world] (process-command world))
               (assoc-in [:world :commandline] "")))

    (\a \b \c \d \e \f \g \h \i \j \k \l \m \n \o \p \q \r \s \t \u \v \w \x \y \z)
    (let [world (:world game)
          {:keys [commandline]} world]
      (assoc-in game [:world :commandline] (str commandline input))) 

    game))

; Input processing -------------------------------------------------------
(defn get-input
  "Gets user's keypress."
  [game screen]
  (assoc game :input (s/get-key-blocking screen)))
