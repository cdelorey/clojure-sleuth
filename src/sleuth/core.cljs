(ns sleuth.core
  (:use [sleuth.ui.core :only [->UI]]
        [sleuth.ui.update :only [update]]
        [sleuth.ui.drawing :only [draw-game]]
        [sleuth.ui.input :only [get-input process-input]]
        [sleuth.world.rooms :only [load-rooms]]
        [sleuth.world.items :only [load-items]]
        [sleuth.world.alibis :only [load-alibis]]
        [sleuth.world.text :only [load-text]]
        [sleuth.entities.guests :only [load-guests]]))



; Data Structures --------------------------------------------------------
(defrecord Game [world uis input])

; Main -------------------------------------------------------------------
(defn run-game [game screen]
  (loop [{:keys [input uis] :as game} game]
    (when (seq uis)
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
              :uis [(->UI :start)]
              :input nil}))

(defn load-text-files
  "Load all game text from files."
  []
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

