(ns sleuth.utils)

;
;http://cemerick.com/2010/08/03/enhancing-clojures-case-to-evaluate-dispatch-values/
(defmacro case+
  "Same as case, but evaluates dispatch values, needed for referring to
   class and def'ed constants as well as java.util.Enum instances."
  [value & clauses]
  (let [clauses (partition 2 2 nil clauses)
        default (when (-> clauses last count (== 1))
                  (last clauses))
        clauses (if default (drop-last clauses) clauses)
        eval-dispatch (fn [d]
                        (if (list? d)
                          (map eval d)
                          (eval d)))]
    `(case ~value
       ~@(concat (->> clauses
                   (map #(-> % first eval-dispatch (list (second %))))
                   (mapcat identity))
           default))))

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

