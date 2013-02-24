(ns sleuth.ui.input
  (:use [sleuth.ui.core :only [->UI instructions]]
        [sleuth.world.core :only [->World load-house]]
        [sleuth.entities.player :only [make-player move-player]]
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
  (let [new-house (rand-nth
                    ["resources/house22.txt" "resources/house22.txt"])
        new-world (->World (load-house new-house) "oh hi" "" {})]
    (-> game
        (assoc :world new-world)
        (assoc-in [:world :entities :player] (make-player new-world))
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

    :left (-> game   
              (update-in [:world] move-player :w)
              (assoc-in [:world :message] (str (get-in game [:world :entities :player :location]))))
    :down (-> game
              (update-in [:world] move-player :s)
              (assoc-in [:world :message] (str (get-in game [:world :entities :player :location]))))
    :up (-> game
            (update-in [:world] move-player :n)
            (assoc-in [:world :message] (str (get-in game [:world :entities :player :location]))))
    :right (-> game
               (update-in [:world] move-player :e)
               (assoc-in [:world :message] (str (get-in game [:world :entities :player :location]))))

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
