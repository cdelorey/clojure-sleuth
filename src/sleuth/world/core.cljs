(ns sleuth.world.core
  (:use [sleuth.world.rooms :only [random-room load-rooms]]
        [sleuth.world.tiles :only [create-house]]
        [sleuth.world.items :only [random-items random-item place-magnifying-glass load-items]]
        [sleuth.world.portals :only [random-passages]]
        [sleuth.entities.guests :only [create-guests]]))



; Data Structures ------------------------------------------------------------
(defrecord World [tiles message commandline entities items flags murder-case secret-passages])


; World Functions ------------------------------------------------------------
(defn new-world [personalized?]
  "Create a new random world."
  (let [world (->World (create-house) "" "" {} {}
                       {:found-magnifying-glass false
                        :found-murder-weapon false
                        :murderer-is-suspicious false
                        :murderer-is-stalking false
                        :game-lost false
                        :assemble false
                        :personalized personalized?}
                       {} {})
        world (assoc-in world [:items] (random-items))
        world (assoc-in world [:murder-case :weapon] (random-item world))
        world (assoc-in world [:murder-case :room] (random-room))
        world (assoc-in world [:murder-case :turn-count] 0)
        world (random-passages world)
        world (create-guests world)
        world (place-magnifying-glass world)]
    world))


