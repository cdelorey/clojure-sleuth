(ns sleuth.ui.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions
  (parse-file "/json5/instructions.txt"))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

