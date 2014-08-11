(ns sleuth.state.core
  (:use [sleuth.utils :only [parse-file]]))

;Definitions ------------------------------------------------------------------
(def instructions "To move around the house use the four arrow keys on the numeric keypad. Objects can be EXAMINED and TAKEN. You can QUESTION people or ask them for an ALIBI. When you have solved the crime, pick up the murder weapon, move to the murder room, ASSEMBLE the suspects, and ACCUSE the guilty party. Good Luck!")

;Data Structures --------------------------------------------------------------
(defrecord State [name])

