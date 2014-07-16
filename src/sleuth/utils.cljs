(ns sleuth.utils
	(:require [ajax.core :refer [GET raw-response-format]]
						[goog.string :as gstring]))

; definitions -----------------------------------------------------------------
(def char-keys
	#{js/ROT.VK_A js/ROT.VK_E js/ROT.VK_I js/ROT.VK_M js/ROT.VK_Q js/ROT.VK_U
		 js/ROT.VK_B js/ROT.VK_F js/ROT.VK_J js/ROT.VK_N js/ROT.VK_R js/ROT.VK_V
		 js/ROT.VK_C js/ROT.VK_G js/ROT.VK_K js/ROT.VK_O js/ROT.VK_S js/ROT.VK_W
		 js/ROT.VK_D js/ROT.VK_H js/ROT.VK_L js/ROT.VK_P js/ROT.VK_T js/ROT.VK_X
		 js/ROT.VK_Y js/ROT.VK_Z})

; functions -------------------------------------------------------------------

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
  "Parses a json5 file and calls a callback function on success."
  [filename callback-function]
	(GET filename {:handler
								 (fn
									 [response]
									 (callback-function (js->clj (.parse js/JSON5 response) :keywordize-keys true)))}))

(defn is-char-key?
	[keycode]
	(contains? char-keys keycode))
