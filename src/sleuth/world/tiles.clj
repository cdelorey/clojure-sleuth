(ns sleuth.world.tiles)

; Constants ------------------------------------------------------------------
(def house-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord Tile [kind glyph color])

(def tiles
  {:hwall     (->Tile :hwall 220 :white)
   :vwall     (->Tile :vwall 219 :white) ;231
   :fwall     (->Tile :fwall 219 :white)
   :stairs    (->Tile :stairs 196 :white)
   :floor     (->Tile :floor 0 :blue)})

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

(defn is-empty? [coord world]
  (and (tile-walkable? (get-tile world coord))
       (not (get-entity-at world coord))))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))

; House -----------------------------------------------------------------------
(defn load-house [filename]
  (with-open  [r (clojure.java.io/reader filename)]
    (let [[cols rows] house-size]
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
