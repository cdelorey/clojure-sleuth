(ns sleuth.core
  (:use [sleuth.state.core :only [->State load-instructions]]
        [sleuth.state.update :only [update]]
        [sleuth.state.drawing :only [draw-game]]
        [sleuth.state.input :only [get-input process-input]]
        [sleuth.world.rooms :only [load-rooms]]
        [sleuth.world.items :only [load-items]]
        [sleuth.world.alibis :only [load-alibis]]
        [sleuth.world.text :only [load-text]]
        [sleuth.entities.guests :only [load-guests]]))

; Data Structures --------------------------------------------------------
(defrecord Game [world states input])

; Main -------------------------------------------------------------------
(defn run-game [game screen]
  (loop [{:keys [input states] :as game} game]
    (when (seq states)
      (recur (if input
               (-> game
                   (dissoc :input)
                   (process-input input))
               (-> game
                   (update)
                   (draw-game screen)
                   (get-input screen)))))))

(defn new-game []
  (map->Game {:world nil
              :states [(->State :start)]
              :input nil}))

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
  ;(loadlib libtcod)
  ;(console-set-custom-font "resources/terminal16x16_gs_ro.png" font-layout-ascii-in-row 16 16)
  ;(console-init-root 80 25 "Test" false tcod-renderer-sdl)
	(.write js/document "test")
  (load-text-files)
  (run-game (new-game) nil))

(set! (.-onload js/window) start)

