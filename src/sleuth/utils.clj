(ns sleuth.utils
  (:require '[clj-yaml.core :as yaml]))

(defn get-lines-from-file [filename]
  "Returns a sequence of the lines in filename."
  (with-open [r (clojure.java.io/reader filename)]
    (doall (line-seq r))))

(def test-string (yaml/parse-string
                   (slurp "resources/instructions.yaml")))


