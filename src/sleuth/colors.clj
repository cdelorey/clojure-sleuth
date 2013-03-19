(ns sleuth.colors
  (:use [sleuth.libtcod :only [color-rgb]]))

; Data structures ----------------------------------------------------------
; colors
(declare ^:dynamic *colors*)

; colors map
(def mcolors
  {:color-black [0 0 0]
   :color-light-red	[255 0 0]
   :color-light-green	[0 255 0]
   :color-yellow	[255 255 0]
   :color-light-blue	[0 0 255]
   :color-light-magenta [255 0 255]
   :color-light-cyan	[0 255 255]
   :color-high-white	[255 255 255]
   :color-gray	[128 128 128]
   :color-red	[128 0 0]
   :color-green	[0 128 0]
   :color-brown	[128 128 0]
   :color-blue	[0 0 128]
   :color-magenta	[128 0 128]
   :color-cyan	[0 128 128]
   :color-white	[192 192 192]})

; color functions -------------------------------------------------------
(defn initialize-colors
  []
  (into {} (for [[k v] mcolors]
             [k (color-rgb v)])))

(defn random-color
  "Return a random color from the colors map"
  []
  (println (rand-nth (keys *colors*)))
  (rand-nth (vals *colors*)))