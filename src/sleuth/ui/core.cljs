(ns sleuth.ui.core)

(def yaml (js/require "js-yaml"))
(def fs (js/require "fs"))

;Definitions ------------------------------------------------------------------
(def instructions
  (.safeLoad yaml (.readFileSync fs "resources/instructions.yaml" "utf8")))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

