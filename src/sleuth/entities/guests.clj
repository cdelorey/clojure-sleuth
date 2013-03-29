(ns sleuth.entities.guests
  (:use [sleuth.world.rooms :only [random-coords random-room]]
        [sleuth.utils :only [keyword-to-string]]))

; Data Structures --------------------------------------------------------------------------
(defrecord Guest [name alibi num-questions location description])

(def guest-names
  [:gerald-brisbane
   :esther-brisbane
   :edward-brisbane
   :david-malone
   :maude-crompton
   :earl-crompton
   :victoria-crompton])

(def guest-descriptions
  {:living-room ["%s is lounging on the sofa, reading a newspaper."
            "%s is lounging on the divan, reading a newspaper."
            "%s is lying on the divan."
            "%s is standing next to the divan."]
   :conservatory ["%s sits at the piano playing a funeral march." 
                  "%s sits at the piano playing a somber concerto."
                  "%s sits at the piano playing a lively sonata."
                  "%s sits at the piano playing a lilting melody."
                  "%s sits at the piano playing a deadly dirge."]
   :secret-passage ["%s is hiding in a corner of the passageway."
                    "%s is poking around in one corner of the corridor."
                    "%s is moving furtively through the passage."]
   :pantry ["%s is searching through the tins."
            "%s is rummaging through the foodstuffs."
            "%s is snacking from the tins."]
   :dining-room ["%s sits at the dining room table having a snack."
                 "%s is supping at the dining room table."
                 "%s is examining the silver serving set."]
   :kitchen ["%s is looking through the contents of the refrigerator."
             "%s is making a sandwich."
             "%s is studying a calorie chart hanging on the wall."
             "%s is preparing a meal."]
   :guest-room ["%s is hiding under the bed."
                "%s is pacing back and forth."
                "%s is lying on the bed."
                "%s is hiding behind the door."]
   :study ["%s is sitting at the desk reading some papers."
           "%s sits at the desk, apparently preoccupied."
           "%s is searching through the papers on the desk."
           "%s is reading some legal papers at the desk."]
   :master-bedroom ["%s is searching under the covers of the bed."
                    "%s is looking underneath the bed."
                    "%s is admiring the craftsmanship of the dresser."]
   :library ["%s is sitting in a chair reading 'Catch-22'."
             "%s is sitting in a chair reading 'The Mousetrap'"
             "%s sits in a chair reading 'A Streetcar Named Desire'"
             "%s is sitting in a chair reading 'Valley of the Dolls'"]
   :master-bathroom ["%s is looking intently into the mirror."
                    "%s is gazing raptly at the mirror."
                    "%s turns from the vanity to greet you."
                    "%s turns from the vanity, shocked to see you."]})

; Guest Functions -----------------------------------------------------------------------------
(defn capitalize-name 
  [n]
  "Capitalizes a name string."
  (let [caps-name (map clojure.string/capitalize (clojure.string/split n #" "))]
    (str (first caps-name) " " (second caps-name))))

(defn keyword-to-name
  "Converts a keyword to a capitalized name."
  [word]
  (capitalize-name (keyword-to-string word)))

(defn random-name
  "Returns a random name from the given names list"
  [names]
  (rand-nth names))

(defn get-guests
  "Returns a list of guests from the given names with random locations.
  
  names is a vector of keywords."
  [names]
  (into {} (for [n names]
             [n (->Guest (keyword-to-name n) " " 0 [0 0] " ")]))) 

(defn place-guest
  [guest-name guest world]
  (let [guests (get-in world [:entities :guests])
        rooms (into [] (for [[k v] (get-in world [:entities :guests])]
                         (:room v)))
        old-room (get-in guests [guest-name :room])
        room (random-room rooms)
        coords (random-coords room)
        description (rand-nth (room guest-descriptions))]
    (as-> world world
          (assoc-in world [:entities :guests guest-name :room] room)
          (assoc-in world [:entities :guests guest-name :location] coords)
          (assoc-in world [:entities :guests guest-name :description] description))))

(defn place-guests
  "Moves all of the guests in guest-list.
  
  guest-list is a vector of keyword names."
  [guest-list world]  
  (loop [guest-list guest-list 
         world world]
    (if (empty? guest-list)
      world
      (do
        (let [guest-name (first guest-list)
              guest (get-guests [guest-name])
              world (assoc-in world [:entities :guests guest-name] (guest-name guest))
              world (place-guest guest-name guest world)]
          (recur (rest guest-list) world))))))

(defn create-guests
  [world]
  "Creates guests with their alibis"
  (let [names (shuffle guest-names)
        [victim
         murderer 
         alone 
         suspect1
         suspect2
         suspect3
         suspect4] names
        guest-list (remove #{victim} guest-names)
        world (place-guests guest-list world)]
    (-> world
        (assoc-in [:murder-case :victim] victim)
        (assoc-in [:murder-case :murderer] murderer)
        (assoc-in [:entities :guests murderer :alibi] :murderer)
        (assoc-in [:entities :guests alone :alibi] :alone)
        (assoc-in [:entities :guests suspect1 :alibi] suspect2)
        (assoc-in [:entities :guests suspect2 :alibi] suspect1)
        (assoc-in [:entities :guests suspect3 :alibi] suspect4)
        (assoc-in [:entities :guests suspect4 :alibi] suspect3))))
