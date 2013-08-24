(ns sleuth.utils)

(defn keywordize
  "Turns a string into a valid clojure keyword."
  [input]
  (str ":" (clojure.string/replace input #" " "-")))

(defn keyword-to-string
  "Converts a keyword to a string, replacing any dashes with spaces."
  [word]
  (clojure.string/replace (name word) #"-" " "))

(defn capitalize-name
  [n]
  "Capitalizes a name string."
  (let [caps-name (map clojure.string/capitalize (clojure.string/split n #" "))]
    (str (first caps-name) " " (second caps-name))))

(defn keyword-to-name
  "Converts a keyword to a capitalized name."
  [word]
  (capitalize-name (keyword-to-string word)))

(defn keyword-to-first-name
  "Converts a keyword to a capitalized name and returns only the first name."
  [word]
  (first (clojure.string/split (keyword-to-name word) #" ")))


(defn abs [i]
  (if (neg? i)
    (- i)
    i))

(defn get-lines-from-file
  "Returns a sequence of the lines in filename."
  [filename]
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

(defmacro and-as-> [expr sym & body]
  `(as-> ~expr ~sym
     ~@(map (fn [b] `(and ~sym ~b)) (butlast body))
     ~(last body)))

