(ns sleuth.personalize)

; Data Structures ------------------------------------------------------------
(defrecord Input-box [x y data])
(defrecord Gui [box-one box-two])
(defrecord Personalize [gui current-box name-list suspect-number])


; Personalize functions ------------------------------------------------------
(defn new-personalize
  "Returns a properly created personalize structure"
  []
  (->Personalize (->Gui
                   (->Input-box 30 10 "")
                   (->Input-box 30 12 ""))
                 :box-one
                 ()
                 1))

(defn swap-current-box
  [box]
  (if (= box :box-one) :box-two :box-one))





