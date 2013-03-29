(ns sleuth.world.alibis
  (:use [sleuth.entities.guests :only [keyword-to-name]]))

; Alibi components ----------------------------------------------------------------------------

; Openers
; used at beginning of an alibi the first time a suspect is asked.
(def openers 
  ["%s turns swiftly and answers,"
   "%s shrugs nonchalantly and says,"
   "%s answers in an annoyed voice,"
   "%s turns to face you and replies,"
   "%s forces a laugh aand replies,"
   "%s draws you closer and whispers,"
   "%s replies in an angry voice,"
   "%s turns away from you and murmurs,"
   "%s sneers wickedly and replies,"
   "%s sighs heavily and replies,"
   "%s replies haughtily."
   "%s turns to face you and whispers,"
   "%s laughs lighty and replies,"
   "%s avoids your eyes and murmers,"
   "%s replies distractedly,"
   "%s turns to face you and answers,"
   ])

; Repeat-openers
; used at beginning of an alibi when the guest has already been asked
(def repeat-openers
  ["%s turns slowly and responds,"
   "%s seems annoyed, but answers,"
   "%s responds wearily,"
   "%s thinks a moment then says,"
   "%s raises an eyebrow and says,"
   ])

; Alibis
(def alibis
  ["\"%s and I spent the entire evening together in the %s"
   "\"%s and I spent the entire evening in the %s"
   "I spent the evening with %s in the %s"
   "I spent the entire evening with %s in the %s"])

; alone alibi
(def alone-alibi
  ["\"I spent the entire evening alone in the %s"])

; Additions
; Additional things guests will say when asked the first time.
(def additions
  ["And I didn't hear or see 
   anything while I was there. I can't imagine what could have happened!"
   "That's all that I can think of to say."
   "You've got your work cut out for you on this one, inspector. None of
    us cared much for %s, and that's the truth. Frankly, whoever did it
    deserves a medal."
   "And let me tell you inspector, that I deeply resent being questioned
    about %s's death. There is simply no way that I could have killed %s.
    We were much too close."
   "I just want all of you to know that I think %s is better off dead.
    And I think everyone in this house will back me up on that. So why
    don't you go find another case, inspector!"
   "Why, I didn't even find out about %s's death until right before you
    arrived. Isn't it awful. Who do you think could do such a thing!?!"
   "Actually, in my opinion, there's no one in this house with the intellectual
    capacity to commit murder and get away with it. Except for myself, of
    course. Too bad I have an alibi!"
   "So face the facts, inspector. I'm obviously not the person you're
    looking for. Now why don't you try questioning %s. There's one person
    who had a lot to gain."
   "Now you really ought to be running along, inspector, though I must
    admit I've enjoyed our little chat. It's amusing the lengths we must  
    go to for a little excitement in this house."
    "And I'm glad %s's dead. I would have done it myself if I'd had the
    chance. So don't expect any help from me, inspector!"
   "And as I recall, neither of us left at any time while this whole
    ghastly murder business was taking place. I'm afraid I just can't
    help you out, inspector."
    "So, I'm afraid you'll have to look elsewhere for your killer. Though,
    for your own safety, I suggest you refrain from doing too much 
    snooping around."
   "Now, with all due respect, inspector, I'm sick of your meddling.
    Why don't you just leave well enough alone and go back to wherever it
    is you came from."
    "But let me tell you that I'm glad %s was murdered. Life will be just
    a little more pleasant for all of us now."
    "So I guess I'm your prime suspect, inspector. But I think you're
    going to find that I didn't have any reason to murder %s."
    "So, while I'm not exactly greiving over %'s little accident, I 
    simply didn't have the opportunity to kill anyone."
    "Now just between you and me and the walls, inspector, I admired %s
    greatly. Wouldn't want the others to know about my little soft spot
    though."
    "We were working on some... uh... private matters the whole time. 
    Frankly, I have no idea what the other guests were doing when %s
    was murdered."
    "So you see, inspector, there's no way I could have killed %s...
    no matter how much I may have wanted to."
   "So I guess I missed out on my chance to be a prime suspect, didn't I?
    It's such a shame--I would so enjoy being suspected of murder!"  
    "So it's obvious that I didn't kill %s. Now why don't you let me be."
    "So, as much as I wanted to see %s dead, I just didn't have the 
    opportunity. Now, why don't you just cross my name off your silly
    little suspect list and go pester someone else."
    "We were having a bit of a row ourselves, so I doubt we would have
    noticed anything out of the ordinary going on elsewhere in the house.
    Sorry I can't be of more help."
   "And I must say that we were both mortified to hear of %s's death.
    Everyone was getting along so well. I just can't imagine who would  
    have wanted to see %s dead."
    "Now, ... is that all you wanted to know? I really would like to go
    lie down if you don't mind. All of this murder business has given me
    a splitting headache."
   "Of course, we both despised %s--but then, so did everyone else in the
    house. Frankly, I think you should just leave well enough alone."
   "Of course it comes as no surprise that you would suspect me. I'm sure
    the others have told you how much I hated %s. But just try to prove that
    I did it--just try."
    "O.K.? Now, could you please leave me and let me get back to my work?
    This whole affair has interferred awfully with my schedule."
   "Sorry to disappoint you--my alibi is airtight. And I'm sick and tired
    of trying to prove my innocence to everyone in this house. Sure, I 
    despised %s but I'm not a murderer."
   ])

; Alone-additions
; Additional comments by guest who was alone all night.
(def alone-additions
  ["I realize that's not much of an alibi, but you must believe me when
    I say that I had no reason to kill %s. No reason at all."
   "But just because I was alone, and just because everyone knows I 
    hated %s is no reason to single me out. Is it, inspector?"
   "Now I suppose that looks bad for me, doesn't it? But you must believe
    that I could never have hurt anyone. And I certainly couldn't have 
    killed %s... I just couldn't."
    "And I didn't see or hear anything while I was there. I can't imagine
    what could have happened!"
   "And I'm afraid that nobody saw me. But I hope you're not so silly as 
    to suspect me of the crime. Why, I hardly knew %s, and I certainly
    had no reason to commit murder."])

; Accusations
; Additional things guests will say when asked for the first time
; that include an accusation of another guest.
(def accusations
  ["Now, uh... I'm not one to gossip, but I don't think you could go 
    wrong by looking into %s's story. There's one person who won't lose
    any sleep over %s's death"
    "I'm just so shocked by this whole affair. Murder is such an ugly    
    business, don't you think? Of course, I've heard that %s doesn't
    share my opinion on that point."
   "So, as you can see, I couldn't have had anything to do with this
    heinous crime. Now why don't you go talk to someone who had reason to
    see %s dead. %s, for example."
   "And I simply cannot believe that anyone in this house would have the
    bad taste to commit murder...
    Though I sometimes think that %s is capable of almost anything."
   "Now inspector, I don't think you should be wasting any more of your
    time on me--I'm just not the murdering type. You may want to check
    out %s however. Now there's a murderer."
   "So, as you can see, I have a perfect alibi. And if you ask me, the only
    likely murderer in this household is that sniveling sycophant %s. 
    Enough said?"
   "Now I've always fancied myself a bit of an investigator, inspector,
    and after a great deal of consideration I've concluded that it's
    really %s you should be talking with."
   "Now, if you ask me, %s is the only person who hated %s enough to
    commit murder. Maybe that's who you should be questioning!"
   "Far be it from me to meddle in you affairs, inspector, but I really
    think you should question %s. Why just the other day %s told me that
    %s would be better off dead."
   "So don't you think you're wasting your time with me, inspector?
    Why don't you give your little investigation a boost by checking out
    %s's story."])

; repeats
; said after a repeat opener
(def repeats
  ["\"As I said the last time you asked,"
    "\"How many times must I tell you?"
    "\"Must you ask all of these questions?"
    " \"It's no business of yours!\""
    "\"Like I said before,"
    "\"My, but you're curious!"
    "I've already told you that"
    "What is this, the inquisition?"
    "Will you pay attention!"
    "I've already told you once."
    "I've already told you everything."])

; refuse
; said when a guest refuses to answer questions
(def refuse
  ["%s ignores you completely."
    "%s turns abruptly to face you and says, \"It's no business of yours!\""
    "%s turns angrily and hisses, \"Why don't you mind your own business!"
    "%s does not respond to your question."
    "%s stares defiantly at you, but says nothing."
    "%s pays no attention to your question."])

;when staring at floor
;"%s looks up from the floor and says,"

; added after alibi when being asked for second time
;"I can't think of anything else to add."

; Alibi Functions -----------------------------------------------------------------------------
(defn get-murderer-alibi
  "Returns an alibi for the murderer."
  [murderer world]
  (let [guests (get-in world [:entities :guests])
        alone (ffirst (filter #(= (:alibi (second %)) :alone) guests))
        guests (dissoc guests murderer)
        guests (dissoc guests alone)
        alibi (rand-nth (keys guests))]
    (println "Murderer!")
    ;(println (vec (keys guests)))
    (format (rand-nth alibis) (keyword-to-name alibi) "room")))

(defn get-alibi
  "Returns an alibi string given a guest and an alibi keyword."
  [guest world]
  (let [alibi (get-in world [:entities :guests guest :alibi])]
    (case alibi
      :murderer (get-murderer-alibi guest world)
      :alone (format (first alone-alibi) "room")
      ;default
      (format (rand-nth alibis) (keyword-to-name alibi) "room"))))

(defn create-alibi-message
  "Creates an alibi based on the guest and number of times the guest has been asked."
  [guest times world]
  (get-alibi guest world))