(ns sleuth.world.tiles
	(:require [ajax.core :refer [GET]]))

; Constants ------------------------------------------------------------------
(def house-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord Tile [kind glyph color])

(def tiles
  {:hwall     (->Tile :hwall "▄" :white)
   :vwall     (->Tile :vwall "█" :white) ;231
   :fwall     (->Tile :fwall "█" :white)
   :stairs    (->Tile :stairs "─" :white)
   :floor     (->Tile :floor " " :blue)
   :door      (->Tile :door "│" :white)
   :hdoor     (->Tile :hdoor "─" :white)})

; Querying tiles --------------------------------------------------------------
; https://github.com/sjl/caves/blob/master/src/caves/world/core.clj
;
(defn get-tile-from-tiles [tiles [x y]]
  (get-in tiles [y x] (:bound tiles)))

(defn tile-walkable?
  "Return whether an entity can walk over this type of tile."
  [tile]
  (#{:floor :stairs} (:kind tile)))

(defn get-tile [world coord]
  (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-kind [world coord]
  (:kind (get-tile world coord)))

(defn get-entities-at [world coord]
  (filter #(= coord (:location %))
          (vals (:entities world))))

(defn get-entity-at [world coord]
  (first (get-entities-at world coord)))

(defn is-empty? [coord world]
  (and (tile-walkable? (get-tile world coord))
       (not (get-entity-at world coord))))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))


; Modifying tiles -------------------------------------------------------------
(defn set-tile
  "Sets the tile at the given location to tile-type"
  [world [x y] tile-type]
  (let [new-tiles (:tiles world)
        new-tiles (assoc-in new-tiles [y x] (tiles tile-type))]
    (assoc-in world [:tiles] new-tiles)))



; House -----------------------------------------------------------------------
(def house "****************_______**************** ***************************************#              #       #              # #              #       #              ##              #       #              # #                      #              ##                      #              # #              #       #              ##              #                      # #              #                      ##              #       #              # #______________#       #              ##              #       #______   _____# #              #       #              ##______________#       #              # #              #       #              ##              #       #              # #                      #______________##______________#       #              # #              #       #              ##              #       #______________# #              #                      ##                      #              # #______________#       #              ##              #       #              # #              #       #              ##              #---#                  # #              #   #---#              ##              #---#   #              # #                  #---#              ##              #---#   #              # #              #   #---#              ##*******************   ***************# #*************************************# ")

(defn create-tile
	[ch]
	(case ch
		\# (tiles :vwall)
		\* (tiles :fwall)
		\_ (tiles :hwall)
		\- (tiles :stairs)
		\space (tiles :floor)
		(println ch)))

(defn create-house []
	(let [house-string (vec (partition (first house-size) house))]
		(vec (map #(vec (map (fn[ch](create-tile ch)) %)) house-string))))


