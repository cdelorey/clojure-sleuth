(ns sleuth.world.tiles
	(:require [ajax.core :refer [GET]]))

; Constants ------------------------------------------------------------------
(def house-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord Tile [kind glyph color])

(def tiles
  {:hwall     (->Tile :hwall 220 :white)
   :vwall     (->Tile :vwall 219 :white) ;231
   :fwall     (->Tile :fwall 219 :white)
   :stairs    (->Tile :stairs 196 :white)
   :floor     (->Tile :floor 0 :blue)
   :door      (->Tile :door 179 :white)
   :hdoor     (->Tile :hdoor 196 :white)})

; Querying tiles --------------------------------------------------------------
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
(defn load-house [filename])
;  (let [file-descriptor (.openSync fs filename "r")
;        [cols rows] house-size]
;    (letfn [(read-tile []
;                       (let [tile (char (.readSync fs file-descriptor (js/Buffer. 1) 0 1))]
;                         (case tile
;                           \# (tiles :vwall)
;                           \* (tiles :fwall)
;                           \_ (tiles :hwall)
;                           \- (tiles :stairs)
;                           \space (tiles :floor)
;                           (println tile))))
;            (read-row []
;                      (vec (repeatedly cols read-tile)))]
;      (vec (repeatedly rows read-row)))
;    (.closeSync fs file-descriptor)))
