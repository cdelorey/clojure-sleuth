(ns sleuth.core
  (:use [sleuth.state.core :only [->State load-instructions]]
        [sleuth.state.update :only [update]]
        [sleuth.state.drawing :only [draw-game]]
        [sleuth.state.input :only [get-input process-input key-listener]]
        [sleuth.world.rooms :only [load-rooms]]
        [sleuth.world.items :only [load-items]]
        [sleuth.world.alibis :only [load-alibis]]
        [sleuth.world.text :only [load-text]]
        [sleuth.entities.guests :only [load-guests]]))

; Data Structures --------------------------------------------------------
(defrecord Game [world states input])

; Main -------------------------------------------------------------------
(def frame
	"https://github.com/ibdknox/gambit/blob/master/cljs/game/lib/core.cljs"
  (or (.-requestAnimationFrame js/window)
      (.-webkitRequestAnimationFrame js/window)
      (.-mozRequestAnimationFrame js/window)
      (.-oRequestAnimationFrame js/window)
      (.-msRequestAnimationFrame js/window)
      (fn [callback] (js/setTimeout callback 17))))

(defn game-loop [game screen]
  (let [{:keys [input states] :as game} game]
    (when (seq states)
			(frame #(game-loop
							 (if input
								 (-> game
										 (dissoc :input)
										 (process-input input))
								 (-> game
										 (update)
										 (draw-game screen)
										 (get-input screen)))
							 screen)))))

(defn new-game []
  (map->Game {:world nil
              :states [(->State :start)]
              :input ()}))

(defn load-text-files
  "Load all game text from files."
  []
	(load-instructions "/json5/instructions.txt")
  (load-alibis "/json5/alibis.txt")
  (load-items "/json5/items.txt")
  (load-guests "/json5/guests.txt")
  (load-rooms "/json5/rooms.txt")
  (load-text))

(defn start
  []
	(let [display (js/ROT.Display.)]
		(if (not (.isSupported js/ROT))
			(js/alert "Whoops! The rot.js library is not supported by your browser.")
			(do
				(.appendChild (.-body js/document) (.getContainer display))
				(load-text-files)
				(.addEventListener js/window "keydown" key-listener)
				(game-loop (new-game) display)))))

(set! (.-onload js/window) start)
