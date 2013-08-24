(ns sleuth.world.core
  (:use [sleuth.world.rooms :only [random-room load-rooms]]
        [sleuth.world.tiles :only [load-house]]
        [sleuth.world.items :only [random-items random-item place-magnifying-glass load-items]]
        [sleuth.world.portals :only [random-passages]]
        [sleuth.entities.guests :only [create-guests]]))



; Data Structures ------------------------------------------------------------
(defrecord World [tiles message commandline entities items flags murder-case secret-passages])


; World Functions ------------------------------------------------------------
(defn new-world []
  "Create a new random world."
  (let [new-house (rand-nth ["resources/house22.txt"])
        world (->World (load-house new-house) "" "" {} {}
                       {:found-magnifying-glass false
                        :found-murder-weapon false
                        :murderer-is-suspicious false
                        :murderer-is-stalking false
                        :game-lost false
                        :assemble false}
                       {} {})
        world (assoc-in world [:items] (random-items))
        world (assoc-in world [:murder-case :weapon] (random-item world))
        world (assoc-in world [:murder-case :room] (random-room))
        world (assoc-in world [:murder-case :turn-count] 0)
        world (random-passages world)
        world (create-guests world)
        world (assoc-in world [:items :dining-room] [:magnifying-glass "a magnifying glass"])] ;testing
        ;world (place-magnifying-glass world)]
    world))


