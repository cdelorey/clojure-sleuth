(ns sleuth.ui.core)

;Definitions ------------------------------------------------------------------
(def instructions 
  (list "First page." "Second page." "Third page." "Fourth page."))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])
