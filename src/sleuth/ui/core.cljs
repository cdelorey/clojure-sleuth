(ns sleuth.ui.core
	(:require [ajax.core :refer [GET]]))

(def yaml (js/require "js-yaml"))

;Definitions ------------------------------------------------------------------
(def instructions
  (.safeLoad yaml (GET "/resources/instructions.yaml")))

;Data Structures --------------------------------------------------------------
(defrecord UI [kind])

