(ns sleuth.core
  (:use [sleuth.ui.core :only [->UI]]
        [sleuth.ui.drawing :only [draw-game]]
        [sleuth.ui.input :only [get-input process-input]])
  (:require [lanterna.screen :as s]))

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

(defn main 
  ([] (main :swing false))
  ([screen-type] (main screen-type false))
  ([screen-type block?]
   (letfn [(go []
             (let [screen (s/get-screen screen-type)]
                (s/in-screen screen
                             (run-game (new-game) screen))))]
     (if block?
       (go)
       (future (go))))))

(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                      (args ":swing")   :swing
                      (args ":text")    :text
                      :else             :auto)]
    (main screen-type true)))
