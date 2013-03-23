(ns sleuth.world.core
  (:use [sleuth.world.rooms :only [random-room]]
        [sleuth.world.items :only [random-items random-item place-magnifying-glass]]
        [sleuth.world.portals :only [random-passages]]
        [sleuth.entities.guests :only [create-guests]]))

; Constants ------------------------------------------------------------------
(def world-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord World [tiles message commandline entities items flags murder-case secret-passages])
(defrecord Tile [kind glyph color])
(defrecord Rect [x y width height])

(def tiles
  {:hwall     (->Tile :hwall 220 :white)
   :vwall     (->Tile :vwall 219 :white) ;231
   :fwall     (->Tile :fwall 219 :white)
   :stairs    (->Tile :stairs 196 :white)
   :floor     (->Tile :floor 0 :blue)}) 


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
        world (->World (load-house new-house) "" "" {} {} 
                       {:found-magnifying-glass false
                        :found-murder-weapon false
                        :murderer-is-suspicious false}
                       {} {})
        world (assoc-in world [:items] (random-items))
        world (assoc-in world [:murder-case :weapon] (random-item world))
        world (assoc-in world [:murder-case :room] (random-room))
        world (random-passages world)
        world (assoc-in world [:entities :guests] (create-guests))
        world (assoc-in world [:items :dining-room] [:magnifying-glass "a magnifying glass"])] ;testing
        ;world (place-magnifying-glass world)]
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

(defn is-empty? [coord world]
  (and (tile-walkable? (get-tile world coord))
       (not (get-entity-at world coord))))

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))
