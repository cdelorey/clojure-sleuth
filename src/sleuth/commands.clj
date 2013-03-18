(ns sleuth.commands
  (:use [sleuth.world.rooms :only [get-room-name get-item-name get-item-examination]]
        [sleuth.ui.core :only [->UI]]))

; Helpers -----------------------------------------------------------------------------------------
(defn keywordize
  "Turns a string into a valid clojure keyword."
  ;maybe move this to utils?
  [input]
  (str ":" (clojure.string/replace input #" " "-")))


(defn is-match?
  ; This could be more clearly named
  "Returns true if the given keyword matches the given object string."
  [object word]
  (= (keywordize object) (str word)))

(defn examined?
  "Returns true if a the object in room-name has been examined"
  ; there must be a simpler way to do this.
  [item]
  (let [examined (some #(= % :examined) item)]
    (if examined
      true
      false)))

(defn murder-weapon?
  "Returns true if the given item is the murder weapon."
  [item world]
  (let [murder-weapon (get-in world [:murder-case :weapon])]
    (= murder-weapon item)))

; Commands ----------------------------------------------------------------------------------------
(defn examine
  "Command to examine an object."
  [object world]
  (let [room-name (get-room-name (get-in world [:entities :player :location]))
        item-name (get-item-name room-name world)
        found-magnifying-glass (get-in world [:flags :found-magnifying-glass])]
    (cond
     (and (= found-magnifying-glass false)
          (is-match? object item-name)) (assoc-in 
                                       world [:message] "It doesn't look like the murder weapon to me.\nOf course, without the magnifying glass, it's hard for me to be certain.")
     
     (and (= found-magnifying-glass true)
          (murder-weapon? item-name world)
          (is-match? object item-name)) (-> world
                                         (assoc-in [:message] "There are traces of blood on it! This must be the murder weapon!")
                                         (update-in [:items room-name] conj :examined))
     
     (and (= found-magnifying-glass true)
          (is-match? object item-name)) (-> world
                                       (assoc-in [:message] (get-item-examination item-name))
                                       (update-in [:items room-name] conj :examined)) 
     
     :else (assoc-in world [:message] "I don't see anything like that around here"))))

     
(defn pick-up 
  "Command to pick-up the item specified in arguments."
  [object world]
  (let [room-name (get-room-name (get-in world [:entities :player :location]))
        item-name (get-item-name room-name world)
        item (get-in world [:items room-name])]
    (cond
     (and (or (= object "magnifying glass") (= object "glass")) 
          (= item-name :magnifying-glass)) (-> world 
                                          (update-in [:items] dissoc room-name)
                                          (assoc-in [:message] "You are now carrying the magnifying glass.")
                                          (assoc-in [:flags :found-magnifying-glass] true))
     
     (and (is-match? object item-name)
          (not (examined? item))) (assoc-in world [:message] "You really should examine the object carefully with a magnifying glass before\nyou try to take it.")
     
     (and (is-match? object item-name)
          (examined? item)
          (not (murder-weapon? item-name world))) (assoc-in world [:message] "This is not the murder weapon, so there's not much point in taking it.")
     
     (and (is-match? object item-name)
          (examined? item)
          (murder-weapon? item-name world)) (-> world
                                                (update-in [:items] dissoc room-name)
                                                (assoc-in [:message] "You are now carrying the murder weapon.")
                                                (assoc-in [:flags :found-murder-weapon] true))
     
     :else (assoc-in world [:message] "I don't see anything like that around here."))
     ))


(defn help
  "Displays short instructions."
  [world]
  (assoc-in world [:message] "To move around the house use the four arrow keys on the numeric keypad.\nObjects can be EXAMINED and TAKEN. You can QUESTION people or ask them for an\nALIBI. When you have solved the crime, pick up the murder weapon, move to the\nmurder room, ASSEMBLE the suspects, and ACCUSE the guilty party. Good Luck!"))


(defn restart
  "Restarts game and returns to menu."
  [game]
  (assoc game :uis [(->UI :menu)]))


(defn quit
  "Exits game"
  [game]
  (assoc game :uis []))

; Process-command -----------------------------------------------------------------------------  
(defn process-command
  "Parse commands entered on commandline.
  
  Commands that require access to the UIs take a game, all other commands take a world object."
  [game]
  (let [world (:world game)
        command (:commandline world)
        first-command (first (clojure.string/split command #" "))
        rest-command (clojure.string/replace-first command (str first-command " ") "")]
    (cond
     (= first-command "get") (assoc-in game [:world] (pick-up rest-command world))
     
     (= first-command "examine") (assoc-in game [:world] (examine rest-command world))
     
     (= first-command "help") (assoc-in game [:world] (help world))
     
     (= first-command "restart") (restart game)
     
     (= first-command "quit") (quit game)
     
     :else (assoc-in game [:world :message] 
                      "I'm sorry, but I can't seem to make out what you're trying to say."))))


