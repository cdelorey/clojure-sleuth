(ns sleuth.ui.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions
  (parse-file "/resources/instructions.yaml"))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

