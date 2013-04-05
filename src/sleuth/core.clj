(ns sleuth.core
  (:use [sleuth.ui.core :only [->UI]]
        [sleuth.ui.drawing :only [draw-game]]
        [sleuth.ui.input :only [get-input process-input]]
        [sleuth.world.core :only [load-text-files]]
        [sleuth.libtcod]
        [clj-native.direct :only [loadlib]]))



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
                   (draw-game screen)
                   (get-input screen)))))))

(defn new-game []
  (map->Game {:world nil
              :uis [(->UI :start)]
              :input nil}))

(defn -main
  []
  (loadlib libtcod)
  (console-set-custom-font "resources/terminal16x16_gs_ro.png" font-layout-ascii-in-row 16 16)
  (console-init-root 80 25 "Test" false tcod-renderer-sdl)
  (load-text-files)
  (run-game (new-game) root))
