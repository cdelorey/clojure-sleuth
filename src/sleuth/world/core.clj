(ns sleuth.world.core
  (:use [sleuth.world.rooms :only [random-items]]))

; Constants ------------------------------------------------------------------
(def world-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord World [tiles message commandline entities items])
(defrecord Tile [kind glyph color])
(defrecord Rect [x y width height])

(def tiles
  {:hwall     (->Tile :hwall "\u2583" :white)
   :vwall     (->Tile :vwall "\u2503" :white)
   :fwall     (->Tile :fwall "\u2588" :white)
   :stairs    (->Tile :stairs "-" :white)
   :floor     (->Tile :floor " " :blue)}) 


; World Functions ------------------------------------------------------------
(defn load-house [filename]
  (with-open  [r (clojure.java.io/reader filename)]
    (let [[cols rows] world-size]
      (letfn [(read-tile []
                (let [tile (char (.read r))]
                  (case tile
                    \# (tiles :vwall)
                    \* (tiles :fwall)
                    \_ (tiles :hwall)
                    \- (tiles :stairs)
                    \space (tiles :floor)
                    (println tile))))
              (read-row []
                (vec (repeatedly cols read-tile)))]
        (vec (repeatedly rows read-row))))))


(defn new-world []
  (let [new-house (rand-nth ["resources/house22.txt"])
        world (->World (load-house new-house) "" "" {} {})
        world (assoc-in world [:items] random-items)]
    world))
    


; Querying the world ---------------------------------------------------------
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

(defn is-empty? [world coord]
  (and (tile-walkable? (get-tile world coord))
       (not (get-entity-at world coord))))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))
