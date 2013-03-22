(ns sleuth.world.rooms
  (:use [sleuth.world.items :only [get-item-description room-items]]))

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


(def room-descriptions
  {:living-room "You have entered the living room. A large divan sits in the middle of the room.\nMany overstuffed chairs are arranged around the edge of the room."
   :dining-room "You are now in the dining room. A large oak table dominates the room. A silver \nserving set sits on a smaller table in one corner of the room."
   :secret-passage "This is a secret passageway. The corridor is covered with dusty cobwebs. \nIn the dim light you can make out the traces of footprints on the dusty floor."
   :kitchen "You are in a spacious kitchen. A large butcher block table sits in the middle \nof the room. The walls are lined with bright brass pots and pans."
   :pantry "You are standing in a pantry. The shelves are filled with a dazzling array of \nimported delicacies. A large tin of salmon lies on the floor."
   :conservatory "This is the conservatory. A baby grand piano sits on a mink throw rug."
   :main-hall "You are walking down the main hall. The walls are covered with a variety of \nold master paintings."
   :main-hall2 "You are walking down the main hall. The walls are covered with a variety of \nold master paintings."
   :lower-stairs "You are on the lower steps of the staircase. The oak steps are highly polished \nand somewhat slippery."
   :upper-stairs "You are standing on the upper half of the staircase. One of the steps creaks \nrather loudly."
   :upstairs-hall "You are walking through the upstairs hall. Discrete oriental silk screens\ndecorate the walls."
   :upstairs-hall2 "You are walking through the upstairs hall. Discrete oriental silk screens\ndecorate the walls."
   :study "You are standing in a well appointed study. Many papers are strewn about. \nUnlike the other rooms in the house, the study appears very lived-in."
   :master-bedroom "This is the master bedroom. A canopied bed dominates the room. To one side \nof the bed is a beautifully hand-carved dresser."
   :master-bathroom "You are standing in the master bathroom. Marble fixtures adorn the room."
   :library "This room is a comfortably furnished library. The shelves are lined with a \nlarge selection of dusty books."
   :guest-room "You are standing in the guest room. The room is decorated in a tasteful \noriental style, but looks rather barren."
   :doorway-living-room "This doorway leads into the living room."
   :doorway-dining-room "This doorway leads into the dining room."
   :doorway-kitchen "This door leads into the kitchen."
   :doorway-conservatory "This is the entrance to the conservatory."
   :doorway-master-bathroom "This is the entrance to the bathroom."
   :doorway-library "This door leads into the library."
   :doorway-guest-room "This door leads into the guest room."
   :doorway-master-bedroom "This is the entrance to the master bedroom"
   :doorway-study "This door leads into the study."
   :doorway-pantry "This is the entrance to the pantry."})


; Room Functions ---------------------------------------------------------------
(defn in-rect? 
 "Return true if the given coordinates are contained in the given rect."
 [[x-coord y-coord] rect]
  (let [{:keys [x y width height]} rect]
    (if (and
      (and (>= x-coord x) (< x-coord (+ x width)))
      (and (>= y-coord y) (< y-coord (+ y height))))
  true
  false)))

(defn get-room 
  "Return the room containing the coordinates [x y]."
  [[x y]]
  (filter #(in-rect? [x y] (second %)) room-rects)) 

(defn get-room-name
  "Return the name of the room for the coordinates [x y]"
  [[x y]]
  (first (keys (get-room [x y]))))

(defn get-room-description
  "Return a room description for the coordinates [x y]"
  [[x y] world]
  (let [room-name (get-room-name [x y])]
    (if-let [item-description (get-item-description room-name world)]
      (str (room-name room-descriptions)  "\n" item-description)
      (room-name room-descriptions))))

(defn random-room
  "Returns a random room name."
  []
  (rand-nth (keys room-items)))

(defn random-coords
  "Returns the coordinates of a random location in a random room"
  []
  (let [room (random-room)
        rect (room room-rects)
        x (+ (rand-int (:width rect)) (:x rect))
        y (+ (rand-int (:height rect)) (:y rect))]
    [x y]))


