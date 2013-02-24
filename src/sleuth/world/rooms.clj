(ns sleuth.world.rooms)

(def room-rects
  {:living-room (->Rect 1 10 14 6)
   :dining-room (->Rect 1 1 14 6)
   :secret-passage (->Rect 1 8 14 1)
   :kitchen (->Rect 24 1 14 5)
   :pantry (->Rect 24 7 14 3)
   :conservatory (->Rect 24 11 14 5)
   :main-hall (->Rect 16 1 7 12)
   :lower-stairs (->Rect 16 12 3 3)})

;TODO: load this from a file
(def room-descriptions
  {:living-room "You have entered the living room. A large divan sits in the middle of the room.\newline
                Many overstuffed chairs are arranged around the edge of the room."
   :dining-room "You are now in the dining room. A large oak table dominates the room. A silver \newline
                serving set sits on a smaller table in one corner of the room."
   :secret-passage "This is a secret passageway. The corridor is covered with dusty cobwebs. \newline
                   In the dim light you can make out the traces of footprints on the dusty floor."
   :kitchen "You are in a spacious kitchen. A large butcher block table sits in the middle \newline
            of the room. The walls are lined with bright brass pots and pans."
   :pantry "You are standing in a pantry. The shelves are filled with a dazzling array of \newline
           imported delicacies. A large tin of salmon lies on the floor."
   :conservatory "This is the conservatory. A baby grand piano sits on a mink throw rug."
   :main-hall "You are walking down the main hall. The walls are covered with a variety of \newline
              old master paintings."
   :lower-stairs "You are on the lower steps of the staircase. The oak steps are highly polished \newline
                 and somewhat slippery."
   :upper-stairs "You are standing on the upper half of the staircase. One of the steps creaks \newline
                 rather loudly."
   :upstairs-hall "You are walking through the upstairs hall. Discrete oriental sil screens \newline
                  decorate the walls."
   :study "You are standing in a well appointed study. Many papers are strewn about. \newline
          Unlike the other rooms in the house, the study appears very lived-in."
   :master-bedroom "This is the master bedroom. A canopied bed dominates the room. To one side \newline
                   of the bed is a beautifully hand-carved dresser."
   :master-bathroom "You are standing in the master bathroom. Marble fixtures adorn the room."
   :library "This room is a comfortably furnished library. The shelves are lined with a \newline
            large selection of dusty books."
   :guest-room "You are standing in the guest room. The room is decorated in a tasteful \newline
               oriental style, but looks rather barren."})

(def room-items
    {:conservatory {:oboe "An oboe lies nestled in the throw rug."
                    :bust-of-mozart "A bust of mozart sits on top of the piano."
                    :marble-bust-of-beethoven "A marble bust of Beethoven sits on top of the piano."}
     :living-room {:bronze-statuette "A bronze statuette sits on the coffee table."
                   :ornate-hourglass "An ornate hourglass in a heavy silver frame sits on the coffee table."
                   :sterling-silver-candy-dish "A sterling silver candy dish sits on a table next to the divan."}
     :secret-passage {:obsidian-obelisk "An obsidian obelisk lies on the floor"
                      :nondescript-blunt-instrument "A nondescript blunt instrument lies on the floor."
                      :elephant-tusk "An elephant tusk lies in among the cobwebs."}
     :kitchen {:frozen-leg-of-lamb "A frozen leg of lamb is thawing on the table."
               :frozen-quail "A frozen quail lies thawing on the table."
               :silver-bottle-opener "A silver bottle opener lies on the table."}
     :pantry {:jar-of-caviar "A jar of caviar sits on the top shelf."
              :can-of-vichyssiose "A can of vichyssiose protrudes from one of the shelves."
              :tin-of-salmon "A large tin of salmon lies on the floor."}
     :dining-room {:wooden-pepper-mill "A wooden pepper mill sits in the middle of the table."
                   :crystal-decanter "A crystal decanter sits at one end of the table."
                   :candelabra "A rather large candelabra sits in the middle of the table."}
     :guest-room {:marble-egg "A marble egg is perched on the edge of the night table."
                  :antique-oil-lamp "An antique oil lamp sits on the night table."
                  :alabaster-ashtray "An alabaster ashtray sits on the edge of the nighttable."}
     :library {:unabridged-dictionary "An unabridged dictionary sits on a table in the middle of the room."
               :oak-reading-stand "An oak reading stand sits in the middle of the room."
               :potted-fern "A potted fern sits next to one of the chairs."}
     :master-bathroom {:jar-of-cold-cream "A large jar of cold cream lies open on the vanity."
                       :bottle-of-vitamins "A large bottle of vitamins sits on the vanity."
                       :silver-hairbrush "A silver hairbrush lies on the vanity."}
     :master-bedroom {:empty-bottle-of-champagne "An empty bottle of champagne sticks out from under the bed."
                      :gold-gilt-mirror "A gold gilt mirror lies by the side of the bed."
                      :jewel-box "A jewel box sits on top of the dresser."}
     :study {:empty-pewter-mug "An empty pewter mug sits on top of the desk."
             :granite-paperweight "A granite paperweight sits on top of some papers on the desk."
             :brass-plaque "A large brass plaque hangs behind the desk."}})

; if item has no description, print "there is nothing unusual about the /thing/"
(def item-descriptions
  {:bust-of-mozart "It seems a rather cheap plaster bust"
   :sterling-silver-candy-dish "The dish is empty"
   :obsidian-obelisk "It seems to be made of a black, glass-like substance."
   :empty-bottle-of-champagne "It is a bottle of Chateau Rothschild 1966."
   :jar-of-cold-cream "It is a very heavy glass jar."
   :marble-egg "It seems to be purely decorative."
   :bronze-statuette "It seems to be a likeness of cupid"
   :crystal-decanter "The decanter appears to be empty."
   :frozen-quail "It looks quite delicious -- if you like quail."
   :jar-of-caviar "It seems to be full of fish eggs!"
   :antique-oil-lamp "It seems to serve no useful purpose."
   :oak-reading-stand "The oak stand is quite heavy."
   :bottle-of-vitamins "The vitamins are fortified with iron!"
   :gold-gilt-mirror "The mirror is broken."
   :empty-pewter-mug "The mug appears to be quite old."
   :silver-bottle-opener "The bottle opener appears to get a great deal of use."
   :granite-paperweight "There is nothing special about the paperweight."
   :can-of-vichyssiose "It seems to be some kind of soup."
   :elephant-tusk "It seems to be a genuine ivory tusk."
   :frozen-leg-of-lamb "It looks quite delicious -- if you like lamb."
   :potted-fern "It seems to be in need of water."
   :jewel-box "The jewel box is quite securely locked."
   :brass-plaque "It says, 'Tennis League Champions -- 1963'."
   :marble-bust-of-beethoven "It is a run-of-the-mill marble bust."
   :tin-of-salmon "It is an unusually large tin of salmon."
   :ornate-hourglass "It is an attractive timepiece, but not very practical."
   :candelabra "This is a very ornate silver candelabra."})

