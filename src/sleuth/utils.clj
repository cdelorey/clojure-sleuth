(ns sleuth.utils)

(defn get-lines-from-file [filename]
  "Returns a sequence of the lines in filename."
  (with-open [r (clojure.java.io/reader filename)]
    (doall (line-seq r))))

(defn map2d
  "Map a function across a two-dimensional sequence."
  [f s]
  (map (partial map f) s))

(defn slice
  "Slice a sequence."
  [s start width]
  (->> s
       (drop start)
       (take width)))

(defn shear
  "Shear a two-dimensional sequence, returning a smaller one."
  [s x y w h]
  (map #(slice % x w)
       (slice s y h)))

(defn enumerate [s]
  (map-indexed vector s))

