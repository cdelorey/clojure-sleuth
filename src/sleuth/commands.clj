(ns sleuth.commands
  (:use [sleuth.world.rooms :only [get-room-name get-item-name get-item-examination]]))

; Helpers -----------------------------------------------------------------------------------------
(defn keywordize
  "Turns a string into a valid clojure keyword."
  ;maybe move this to utils?
  [input]
  (str ":" (clojure.string/replace input #" " "-")))


(defn is-match? 
  "Returns true if the given keyword matches the given object string."
  [object word]
  (= (keywordize object) (str word)))

; Commands ----------------------------------------------------------------------------------------
(defn examine
  "Command to examine an object."
  [object world]
  (let [room-name (get-room-name (get-in world [:entities :player :location]))
        item (get-item-name room-name world)
        found-magnifying-glass (get-in world [:flags :found-magnifying-glass])]
    (cond
     (and (= found-magnifying-glass false)
          (is-match? object item)) (assoc-in 
                                       world [:message] "It doesn't look like the murder weapon to me.\nOf course, without the magnifying glass, it's hard for me to be certain.")
     
     (and (= found-magnifying-glass true)
          (is-match? object item)) (assoc-in world [:message] (get-item-examination item))
     
     :else (assoc-in world [:message] "I don't see anything like that around here"))))

     
(defn pick-up 
  "Command to pick-up the item specified in arguments."
  [object world]
  (let [room-name (get-room-name (get-in world [:entities :player :location]))
        item (get-item-name room-name world)]
    (cond
     (and (or (= object "magnifying glass") (= object "glass")) 
          (= item :magnifying-glass)) (-> world 
                                          (update-in [:items] dissoc room-name)
                                          (assoc-in [:message] "You are now carrying the magnifying glass.")
                                          (assoc-in [:flags :found-magnifying-glass] true))
     
     ;(and (= (keywordize object) item)
     ;     ("item has not been examined"))
     
     ;(and (= (keywordize object) item)
     ;     ("item has been examined")
     ;     ("item is not the murder weapon"))
     
     ;(and (= (keywordize object) item)
     ;     ("item has been examined")
     ;     ("item is the murder weapon"))
     
     :else (assoc-in world [:message] "I don't see anything like that around here."))
     ))

; Process-command -----------------------------------------------------------------------------  
(defn process-command
  "Parse commands entered on commandline."
  [world]
  (let [command (:commandline world)
        first-command (first (clojure.string/split command #" "))
        rest-command (clojure.string/replace-first command (str first-command " ") "")]
    (cond
     (= first-command "get") (pick-up rest-command world)
     
     (= first-command "examine") (examine rest-command world)
     
     :else (assoc-in world [:message] 
                      "I'm sorry, but I can't seem to make out what you're trying to say."))))


