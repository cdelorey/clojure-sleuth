(ns sleuth.commands
  (:use [sleuth.world.rooms :only [get-room-name get-item-name]]))

(defn keywordize
  "Turns a string into a valid clojure keyword."
  ;maybe move this to utils?
  [input]
  (str ":" (clojure.string/replace input #" " "-")))

(defn pick-up 
  "Command to pick-up the item specified in arguments."
  [arguments world]
  (let [room-name (get-room-name (get-in world [:entities :player :location]))
        item (get-item-name room-name world)]
    (cond
     (println item)
     (println arguments)
     (and (or (= arguments "magnifying glass") (= arguments "glass")) 
          (= item :magnifying-glass)) (-> world 
                                          (assoc-in [:message] "You are now carrying the magnifying glass.")
                                          (assoc-in [:flags :found-magnifying-glass] true)
                                          (dissoc-in [:items] :magnifying-glass))
     
                                          ;remove magnifying glass from room
     
     ;(and (= (keywordize arguments) item)
     ;     ("item has not been examined"))
     
     ;(and (= (keywordize arguments) item)
     ;     ("item has been examined")
     ;     ("item is not the murder weapon"))
     
     ;(and (= (keywordize arguments) item)
     ;     ("item has been examined")
     ;     ("item is the murder weapon"))
     
     :else (assoc-in world [:message] "I don't see anything like that around here."))
     ))

(defn process-command
  "Parse commands entered on commandline."
  [world]
  (let [command (:commandline world)
        first-command (first (clojure.string/split command #" "))
        rest-command (clojure.string/replace-first command (str first-command " ") "")]
    (println rest-command)
    (cond
     (= first-command "get") (pick-up rest-command world) 
     
     :else (assoc-in world [:message] 
                      "I'm sorry, but I can't seem to make out what you're trying to say."))))


