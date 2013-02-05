(ns sleuth.ui.drawing
  (:require [lanterna.screen :as s]))

; Definitions -------------------------------------------------------------
(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))

; Start -------------------------------------------------------------------
(defmethod draw-ui :start [ui game screen]
  (s/put-sheet screen 0 0
               ["This is a test."
                ""
                "Press any key."]))

; Game --------------------------------------------------------------------
(defn draw-game [game screen]
  (s/clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen)
  game)
