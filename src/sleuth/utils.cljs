(ns sleuth.utils
	(:require [ajax.core :refer [GET raw-response-format]]
						[goog.string :as gstring]))

(defn keywordize
  "Turns a string into a valid clojure keyword, replacing any spaces with dashes."
  [input]
  (keyword (clojure.string/replace input #" " "-")))

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
	(GET filename {:handler #(seq (clojure.string/split (str %) "\n"))}))

(defn parse-file
  "Parses a json5 file and returns a clojure object"
  [filename]
	(GET filename {:handler
								 #(js->clj (.parse js/JSON5 %) :keywordize-keys true)}))
