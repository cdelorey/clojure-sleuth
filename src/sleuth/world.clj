(ns sleuth.world)

; Constants ------------------------------------------------------------------
(def world-size [79 17])

; Data Structures ------------------------------------------------------------
(defrecord World [tiles message commandline entities])
(defrecord Tile [kind glyph color])

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



