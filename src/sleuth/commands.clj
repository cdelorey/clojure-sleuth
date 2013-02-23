(ns sleuth.commands)

(defn process-command [world]
  (let [command (:commandline world)]
    (cond
      :else (assoc-in world [:message] 
                      "I'm sorry, but I can't seem to make out what you're trying to say."))))
