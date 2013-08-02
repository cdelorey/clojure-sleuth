(ns sleuth.commands
  (:use [sleuth.world.rooms :only [get-room-name get-room current-room murder-room get-current-guest]]
        [sleuth.world.items :only [get-item-name get-item-examination]]
        [sleuth.ui.core :only [->UI]]
        [sleuth.utils :only [keywordize abs keyword-to-first-name]]
        [sleuth.world.alibis :only [create-alibi-message get-lose-questioning]]
        [sleuth.entities.player :only [get-player-location]]))

; Helpers -----------------------------------------------------------------------------------------
(defn is-match?
  ; This could be more clearly named
  "Return true if the given keyword matches the given object string."
  [object word]
  (= (keywordize object) (str word)))

(defn examined?
  "Return true if a the object in room-name has been examined"
  ; there must be a simpler way to do this.
  [item]
  (let [examined (some #(= % :examined) item)]
    (if examined
      true
      false)))

(defn murder-weapon?
  "Return true if the given item is the murder weapon."
  [item world]
  (let [murder-weapon (get-in world [:murder-case :weapon])]
    (= murder-weapon item)))

(defn close-enough?
  "Return true if the player is too far away for the guest to hear."
  [[player-x player-y] [guest-x guest-y]]
  (let [x-distance (abs (- player-x guest-x))
        y-distance (abs (- player-y guest-y))]
    (if (or (> x-distance 3) (> y-distance 3))
      false
      true)))

; Commands ----------------------------------------------------------------------------------------
(defn examine
  "Command to examine an object."
  [object world]
  (let [room-name (current-room world)
        item-name (get-item-name room-name world)
        found-magnifying-glass (get-in world [:flags :found-magnifying-glass])]
    (cond
     ; examine the floor
     (= object "floor")
     (let [guest (get-current-guest world)]
       (if (and (:guests-stare-at-floor (:flags world))
                (not (nil? guest))
                found-magnifying-glass)
        (let [player-location (get-player-location world)
              guest-location (get-in world [:entities :guests guest :location])]
          (if (not (close-enough? player-location guest-location))
            (assoc-in world [:message] (str "I can't see what " (keyword-to-first-name guest) " is looking at from all the way over here."))
            (if (= (current-room world) (murder-room world))
            (assoc-in world [:message] "There seem to be blood stains on the floor! This must be the scene of the murder!!")
            (assoc-in world [:message] "There's nothing unusual about the floor."))))
        ;else
        (assoc-in world [:message] "I can't see anything unusual about the floor. But then I am rather nearsighted.")))

     ; examine item without the magnifying glass
     (and (= found-magnifying-glass false)
          (is-match? object item-name)) (assoc-in
                                         world [:message] "It doesn't look like the murder weapon to me.\nOf course, without the magnifying glass, it's hard for me to be certain.")

     ; examine murder weapon
     (and (= found-magnifying-glass true)
          (murder-weapon? item-name world)
          (is-match? object item-name)) (-> world
                                            (assoc-in [:message] "There are traces of blood on it! This must be the murder weapon!")
                                            (update-in [:items room-name] conj :examined))
     ; examine room item
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

(defn guestlist
  "Displays list of guests in the mansion."
  [world message]
  (let [guests (get-in world [:entities :guests])
        guest-names (into [] (for [[k v] guests](:name v)))
        [a b c d e f] guest-names
        message (str message \newline
                     a "        " b "        " c \newline d "        " e "        " f)]
    (assoc-in world [:message] message)))

(defn alibi
  "Displays the alibi of the guest in the same room as the player."
  [world]
  (let [player-location (get-in world [:entities :player :location])
        room (ffirst (get-room player-location))
        guests (get-in world [:entities :guests])
        guest (ffirst (filter #(= (:room (second %)) room) guests))
        guest-location (get-in guests [guest :location])]
    (cond
     (nil? guest)
     (assoc-in world [:message] "But there's nobody here!")

     (not (close-enough? player-location guest-location))
     (assoc-in world [:message] (str (keyword-to-first-name guest)
                                     " can't hear you from all the way over there."))

     :else
     (let [times (get-in world [:entities :guests guest :num-questions])
            message (create-alibi-message guest times world)]
       (if
         (and
          (= guest (get-in world [:murder-case :murderer]))
          (= times 6))
         ;game over - asked murderer too many questions
          (-> world
              (assoc-in [:flags :game-lost] true)
              (assoc-in [:murder-case :lose-text] (get-lose-questioning world)))
         ;display alibi
         (-> world
             (assoc-in [:message] message)
             (update-in [:entities :guests guest :num-questions] inc)))))))

(defn help
  "Displays short instructions."
  [world]
  (assoc-in world [:message] "To move around the house use the four arrow keys on the numeric keypad.\nObjects can be EXAMINED and TAKEN. You can QUESTION people or ask them for an\nALIBI. When you have solved the crime, pick up the murder weapon, move to the\nmurder room, ASSEMBLE the suspects, and ACCUSE the guilty party. Good Luck!"))


(defn assemble
  "Assembles guests in current room to wait for player accusation."
  [world]
  (if (get-in world [:flags :found-murder-weapon])
    (assoc-in world [:flags :assemble] true)
    (assoc-in world [:message] "But I haven't yet found the murder weapon!")))


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

     (= first-command "guestlist")
     (let [message "The houseguests now lurking about the Crompton estate are:"]
       (assoc-in game [:world] (guestlist world message)))

     (= first-command "alibi") (assoc-in game [:world] (alibi world))

     (= first-command "assemble") (assoc-in game [:world] (assemble world))

     (= first-command "restart") (restart game)

     (= first-command "quit") (quit game)

     :else (assoc-in game [:world :message]
                      "I'm sorry, but I can't seem to make out what you're trying to say."))))

(defn process-lose-commands
  "Parse commands entered on the commandline for the lose ui."
  [game]
  (let [world (:world game)
        command (:commandline world)]
    (cond
     (= command "restart") (restart game)
     (= command "quit") (quit game)
     :else game)))

(defn process-accuse-commands
  "Parse commands entered on the commandline for the accuse ui."
  [game]
  (let [world (:world game)
        command (:commandline world)
        first-command (first (clojure.string/split command #" "))]
    (cond
     (= first-command "restart") (restart game)

     (= first-command "quit") (quit game)

     (= first-command "guestlist")
     (let [message "You must ACCUSE one of the six people in the room:"]
       (assoc-in game [:world] (guestlist world message)))

     :else game)))


