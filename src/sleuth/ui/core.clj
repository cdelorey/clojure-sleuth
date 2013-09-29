(ns sleuth.ui.core
  (:require [clj-yaml.core :as yaml]))

;Definitions ------------------------------------------------------------------
(def instructions
  (yaml/parse-string (slurp "resources/instructions.yaml")))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])
