(ns sleuth.ui.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions
  (parse-file "/json5/instructions.json5"))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

