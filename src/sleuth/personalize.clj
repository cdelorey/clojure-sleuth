(ns sleuth.personalize
  (:use [sleuth.utils :only [keywordize]]))

; Data Structures ------------------------------------------------------------
(defrecord Input-box [x y data])
(defrecord Gui [box-one box-two])
(defrecord Personalize [gui current-box name-list suspect-number])


; Personalize functions ------------------------------------------------------
(defn new-personalize
  "Returns a properly created personalize structure"
  []
  (->Personalize (->Gui
                   (->Input-box 30 10 "test1")
                   (->Input-box 30 12 "test2"))
                 :box-one
                 ()
                 1))

(defn swap-current-box
  [box]
  (if (= box :box-one) :box-two :box-one))

(defn switch-to-sleuth-ui
  [game]
  (println "DONE")
  game)

(defn process-personalize-input
  "Process names entered in personalize gui."
  [game]
  (let [current-box (:current-box (:personalize game))]
    (if (= current-box :box-one)
      (assoc-in game [:personalize :current-box] (swap-current-box current-box))
      (let [first-name (get-in game [:personalize :gui :box-one :data])
            last-name (get-in game [:personalize :gui :box-two :data])
            full-name (keywordize (str first-name " " last-name))
            name-list (:name-list (:personalize game))
            game         (-> game
                             (assoc-in [:personalize :name-list] (conj name-list full-name))
                             (assoc-in [:personalize :current-box] (swap-current-box current-box))
                             (assoc-in [:personalize :gui :box-one :data] "")
                             (assoc-in [:personalize :gui :box-two :data] "")
                             (assoc-in [:personalize :suspect-number] (inc (:suspect-number (:personalize game)))))]
        (println name-list)
        (if (= (:suspect-number (:personalize game)) 8)
          (switch-to-sleuth-ui game)
          game)))))



