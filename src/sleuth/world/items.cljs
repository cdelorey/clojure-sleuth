(ns sleuth.world.items
  (:use [sleuth.utils :only [parse-file]]))

(def room-items (atom nil))
(def item-descriptions (atom nil))


; Item Functions --------------------------------------------------------------
(defn load-items
  "Loads item text from filename"
  [filename]
  (let ;[yaml-object (.safeLoad yaml (GET "/resources/items.yaml"))
        ;items-map (js->clj yaml-object :keywordize-keys true)]
        [items-map (parse-file "/resources/items.yaml")]
    (reset! room-items (:room-items items-map))
    (reset! item-descriptions (:item-descriptions items-map))))

(defn random-items []
  (into {} (for [[k v] @room-items]
             [k (rand-nth (vec v))])))

(defn random-item
  "Returns a random item name from the item list."
  [world]
  (let [items (map first (vals (:items world)))]
    (rand-nth items)))

(defn get-item-rooms
  "Returns the room names that contain items."
  []
  (keys @room-items))

(defn get-item-description
  "Returns the description of the item in room-name"
  [room-name world]
  (let [items (get-in world [:items])]
    (second (room-name items))))

(defn get-item-name
  "Returns the name of the item in room-name"
  [room-name world]
  (let [items (get-in world [:items])]
    (first (room-name items))))

(defn get-item-examination
  "Returns the description of an item upon examination."
  [item-name]
  (item-name @item-descriptions))

(defn place-magnifying-glass
  "Add the magnifying glass to a random room in game."
  [world]
  (assoc-in world [:items (rand-nth (keys @room-items))]
            [:magnifying-glass "A magnifying glass is lying on the floor."]))
