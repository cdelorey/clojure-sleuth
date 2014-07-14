(ns sleuth.state.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions (atom nil))

;Data Structures --------------------------------------------------------------
(defrecord State [name])

;Functions --------------------------------------------------------------------
(defn load-instructions
	[filename]
	(parse-file filename #(reset! instructions %)))

