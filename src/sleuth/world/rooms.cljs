(ns sleuth.world.rooms
  (:use [sleuth.world.items :only [get-item-description get-item-rooms]]
        [sleuth.world.tiles :only [set-tile]]
        [sleuth.utils :only [keyword-to-name]]))

; Data Structures -------------------------------------------------------------
(defrecord Rect [x y width height])

(def room-rects
  {:living-room (->Rect 1 10 14 6)
   :dining-room (->Rect 1 1 14 6)
   :secret-passage (->Rect 1 8 14 1)
   :kitchen (->Rect 24 1 14 5)
   :pantry (->Rect 24 7 14 3)
   :conservatory (->Rect 24 11 14 5)
   :main-hall (->Rect 16 1 7 12)
   :main-hall2 (->Rect 20 13 3 4)
   :main-hall3 (->Rect 21 17 1 1)
   :lower-stairs (->Rect 16 13 3 3)
   :master-bathroom (->Rect 41 1 14 4)
   :library (->Rect 41 6 14 5)
   :guest-room (->Rect 41 12 14 4)
   :master-bedroom (->Rect 64 1 14 7)
   :study (->Rect 64 9 14 7)
   :upper-stairs (->Rect 60 13 3 3)
   :upstairs-hall (->Rect 56 1 7 12)
   :upstairs-hall2 (->Rect 56 13 3 3)
   :doorway-living-room (->Rect 15 11 1 1)
   :doorway-dining-room (->Rect 15 3 1 1)
   :doorway-kitchen (->Rect 23 4 1 1)
   :doorway-pantry (->Rect 30 6 3 1)
   :doorway-conservatory (->Rect 23 13 1 1)
   :doorway-master-bathroom (->Rect 55 2 1 1)
   :doorway-library (->Rect 55 8 1 1)
   :doorway-guest-room (->Rect 55 14 1 1)
   :doorway-master-bedroom (->Rect 63 4 1 1)
   :doorway-study (->Rect 63 10 1 1)})


(def room-descriptions (atom nil))
(def yaml (js/require "js-yaml"))
(def fs (js/require "fs"))

; Room Functions ---------------------------------------------------------------
(defn load-rooms
  "Load room text from filename"
  [filename]
  (let [yaml-object (.safeLoad yaml (.readFileSync fs "resources/rooms.yaml" "utf8"))
        rooms-map (js->clj yaml-object :keywordize-keys true)]
    (reset! room-descriptions (:room-descriptions rooms-map))))

(defn in-rect?
 "Return true if the given coordinates are contained in the given rect."
 [[x-coord y-coord] rect]
  (let [{:keys [x y width height]} rect]
    (if (and
      (and (>= x-coord x) (< x-coord (+ x width)))
      (and (>= y-coord y) (< y-coord (+ y height))))
  true
  false)))

(defn get-rect
  "Return the rect for the given room-name"
  [room-name]
  (room-name room-rects))

(defn in-room?
  "Return true if the given coordinates are contained in the given room."
  [[x-coord y-coord] room-name]
  (let [rect (get-rect room-name)]
    (in-rect? [x-coord y-coord] rect)))

(defn get-room
  "Return the room containing the coordinates [x y]."
  [[x y]]
  (filter #(in-rect? [x y] (second %)) room-rects))

(defn get-room-name
  "Return the name of the room for the coordinates [x y]"
  [[x y]]
  (first (keys (get-room [x y]))))

(defn current-room
  "Returns the name of the room the player is currently in."
  [world]
  (let [[x y] (get-in world [:entities :player :location])]
    (get-room-name [x y])))

(defn get-doorway
  "Return the coordinates of the doorway in the current room"
  [room]
  (if (.contains (name room) "-hall")
    nil
    (let [doorway (if (.contains (name room) "doorway")
                    room
                    (keyword (str "doorway-" (name room))))
          doorway-rect (doorway room-rects)]
      [(:x doorway-rect) (:y doorway-rect)])))

(defn lock-current-room
  "Adds a locked door to the doorway of the current room.

  Does nothing if the current room is the secret passage."
  [world]
  (let [room (current-room world)]

    (cond
     (= room :pantry)
     (let [[x y] (get-doorway room)]
       (-> world
             (set-tile [x y] :hdoor)
             (set-tile [(+ x 2) y] :hdoor)
             (set-tile [(+ x 1) y] :hdoor)))

     (= room :secret-passage) world

     :else (set-tile world (get-doorway room) :door))))

(defn get-current-guest
  "Returns the name of the guest in the current room"
  [world]
  (let [room-name (current-room world)]
    (first (remove nil? (for [[k v] (get-in world [:entities :guests])]
                               (if (= (:room v) room-name)
                                 k))))))

(defn murder-room
  "Returns the name of the room where the murder took place"
  [world]
  (get-in world [:murder-case :room]))

(defn get-guest-description
  [room-name world]
  (let [guest-name (get-current-guest world)
        description (get-in world [:entities :guests guest-name :description])]
    (if (= nil description)
      nil
      (if (get-in world [:entities :guests guest-name :is-staring-at-floor])
        (str  (keyword-to-name guest-name) " is staring at the floor.")
        (let [n (get-in world [:entities :guests guest-name :name])]
          (format description n))))))

(defn get-room-description
  "Return a room description for the coordinates [x y]"
  [[x y] world]
  (let [room-name (get-room-name [x y])
        guest-description (get-guest-description room-name world)
        item-description (get-item-description room-name world)
        description (str (room-name @room-descriptions) "\n"  item-description "\n" guest-description)]
    (cond
     (and (= true (get-in world [:flags :murderer-is-stalking])) (not (contains? (set (get-item-rooms)) room-name)))
     (str description "The murderer is now stalking you!")

     (and (= true (get-in world [:flags :murderer-is-suspicious])) (not (contains? (set (get-item-rooms)) room-name)))
     (str description "The murderer has grown suspicious of your investigation!")

     :else description)))

(defn random-room
  "Returns a random room name.

  If a room list is provided, it returns a random room name that is not in the list."
  ([]
   (rand-nth (get-item-rooms)))
  ([room-list]
   (let [room (random-room)]
     (if(some #{room} room-list)
       (recur room-list)
       room))))

(defn random-coords
  "Returns the coordinates of a random location in a room

  If a room is not provided, it returns a random location in a random room."
  ([]
   (let [room (random-room)
         rect (room room-rects)
          x (+ (rand-int (:width rect)) (:x rect))
          y (+ (rand-int (:height rect)) (:y rect))]
     [x y]))
  ([room]
   (let [rect (room room-rects)
         x (+ (rand-int (:width rect)) (:x rect))
         y (+ (rand-int (:height rect)) (:y rect))]
     [x y])))

(defn is-doorway
  "Returns true if the given coordinates are in a doorway."
  [[x y]]
  (let [room-name (get-room-name [x y])]
    (if (.startsWith (name room-name) "doorway")
      true
      false)))

(defn is-item-room
  "Returns true if the given coordinates are in an item room"
  [[x y]]
  (let [room-name (get-room-name [x y])]
    (if (some #{room-name} (get-item-rooms))
      true
      false)))

(defn has-entered-room
  "Returns true if the first coordinates are in a doorway and the second are
  in an item room."
  [[x1 y1] [x2 y2]]
  (if (and (is-doorway [x1 y1]) (is-item-room [x2 y2]))
    true
    false))
