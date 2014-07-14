(ns sleuth.world.text
	(:use [sleuth.utils :only [keyword-to-name keyword-to-string parse-file]]))

;; data structures ------------------------------------------------------------
(def openings (atom nil))

;; text loading ---------------------------------------------------------------
(defn load-opening []
	(parse-file "/json5/opening.txt"
							(fn [openings-vector]
								(reset! openings openings-vector))))

(defn load-text []
  (do
    (load-opening)))

;; text parsing ---------------------------------------------------------------
(defn parse-text
  "Takes a piece of game text (alibi/opening-text/description etc..) and replaces any
  tags (eg. *victim*, *murderer*, ...) with the appropriate name."
  [world text]
  (let [victim (keyword-to-name (:victim (:murder-case world)))
        victim-last (second (clojure.string/split
                              (keyword-to-name (:victim (:murder-case world))) #"\s"))]
    (-> text
        (clojure.string/replace #"\*victim\*" victim)
        (clojure.string/replace #"\*victim-last\*" victim-last))))

(defn contained?
  [key-word input-string]
  (let [thing (keyword-to-string key-word)]
    (if (< (count input-string) 3)
      false
      ())))

(defn random-opening [world]
  (parse-text world (str (rand-nth @openings))))
