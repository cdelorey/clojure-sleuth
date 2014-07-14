(ns sleuth.ui.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions (atom nil))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

;Functions --------------------------------------------------------------------
(defn load-instructions
	[filename]
	(parse-file filename #(reset! instructions %)))

