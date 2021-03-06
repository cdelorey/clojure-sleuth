# TODO:
---------
- fix "can't find variable ROT" bug
- new title screen
- allow for more item name variations on commandline
- add random house colors
- add randomized guest names
- add sounds
- edit writing
- change room descriptions to use text parsing function

Text Re-write
- new items/item descriptions
- new alibis
- change alibi components to have victim and guest tags to be replaced with names. fixes alibi bug.
- new lose text
- new instructions

- put on github pages

-------------------------------------------------------------------------------

# Log:
------

### 02/24/13
__DONE__ get mirror description
__DONE__ write function to get room name from coordinates
__DONE__ make room descriptions display

### 02/25/13
__DONE__ add doorway descriptions
__DONE__ add rects for upstairs rooms

### 02/26/13
__DONE__ fix movement code
__DONE__ fix message display

### 02/27/13
__DONE__ fix incorrect rects
__DONE__ fix hallway rects
__DONE__ add stairs movement

### 03/14/13
__DONE__ add room items
__DONE__ make item descriptions display
__DONE__ put magnifying glass in random room

### 03/17/13
__DONE__ change functions to take a world instead of a game
__DONE__ allow spaces on commandline
__DONE__ add examine command
__DONE__ add examined property to item vectors
__DONE__ add murder weapon selection
__DONE__ add murder room selection
__DONE__ add get command
__DONE__ add help command
__DONE__ change commandline to be case insensitive

### 03/18/13
__DONE__ Port to libtcod
__DONE__ fix movement
__DONE__ fix map characters
__DONE__ update readme
__DONE__ add quit command
__DONE__ add restart command
__DONE__ add cursor character to end of command line
__DONE__ refactor sleuth input processing (make function for movement)

### 03/21/13
__DONE__ remove entity code
__DONE__ add door to secret passage

### 03/22/13
__DONE__ refactor rooms.clj
__DONE__ write guest descriptions
__DONE__ write alibis

### 03/23/13
__DONE__ add guests
__DONE__ determine murderer and victim
__DONE__ determine alibis
__DONE__ make guests display

### 03/27/13
__DONE__ ensure only one guest per room
__DONE__ make guest descriptions display

### 03/28/13
__DONE__ add guest list command
__DONE__ add alibi command

### 03/29/13
__DONE__ add alibi rooms to create-guests function

### 04/01/13
__DONE__ add alibis to create-guests function using get-alibi function

### 04/02/13
__DONE__ increment question count in alibi command

### 04/03/13
__DONE__ implement line wrapping for message output

### 04/04/13
__DONE__ move all alibi text to files
__DONE__ load text from files
__DONE__ add function to create random alibi text

### 04/05/13
__DONE__ move item text to files
__DONE__ move guest text to files

### 04/06/13
__DONE__ move room text to files

### 04/08/13
__DONE__ add turn count
__DONE__ make murderer get suspicious after certain # of turns (180) (only shows up in hallway)
__DONE__ make murderer stalk you (276) (only shows up in hallway)
__DONE__ add distance check for alibi command

### 06/01/13
__DONE__ add lose ui. move common code to functions so both play and lose/win uis can use them.
__DONE__ add update functions to uis
__DONE__ lose if ask murderer too many questions
__DONE__ cleanup testing text

### 06/06/13
__DONE__ add function to check if payer has entered a room

### 06/10/13
__DONE__ lose if being stalked and enter a room.
__DONE__ fix lose-text room-name bug
__DONE__ make guests stare at floor after certain # of turns (suspicious?)

### 07/08/13
__DONE__ add examine floor command

### 07/09/13
__DONE__ add assemble/win ui

### 07/30/13
__DONE__ add door to assemble ui

### 08/01/13
__DONE__ add people to assemble ui (move people function)
__DONE__ add assemble command
__DONE__ assemble command only works if the player has found the murder weapon

### 08/07/13
__DONE__ add accuse command
__DONE__ fix assemble positions for bottom row of room
__DONE__ "everyone is waiting" should only appear if player enters an invalid command, not when any key is pressed
__DONE__ handle case where accused is not a valid guest name
__DONE__ fix assemble text (add correct room name)
__DONE__ fix assemble positions for too far to the right (change x-guest position to place guests in center of room)
__DONE__ fix assemble positions for secret passage (if in secret-passage, guest-y = player-y)
__DONE__ fix accuse bug that thinks a valid guest name is not valid
__DONE__ add closed door to lose room and make sure there is an npc in the room

### 08/08/13
__DONE__ refactor lose-game to game-over to include winning and losing text
__DONE__ switch to game-over ui after an accusation
__DONE__ fix lock door for pantry

### 08/14/13
__DONE__ add random chance for guest to refuse to be questioned

### 08/15/13
__DONE__ add randomness to losing game after being stalked by murderer

### 08/24/13
__DONE__ add randomness to staring at floor
__DONE__ add guest movement
__DONE__ make sure there is a guest staring at floor in murder room
__DONE__ only display guests in same room as player
__DONE__ remove test code (print statements)

### 08/24/13
__DONE__ add basic personalize ui
__DONE__ save personalized names into name list

### 09/21/13
__DONE__ enable use of personalized list in game

### 09/22/13
__DONE__ remove test code (player, magnifying glass placement)
__DONE__ fix staring at floor in examine floor command
__DONE__ make cursor display in personalized ui
__DONE__ add esc to personalize screen
__DONE__ add opening text

### 09/29/13
__DONE__ add opening sequence
__DONE__ refactor multimethods (add default case)

### 10/09/13
__DONE__ fix examine bug

### 05/31/14
__DONE__ switch to cljs/node

### 06/01/14
__DONE__ convert file reading to use node.js

### 06/02/14
__DONE__ convert yaml code to use javascript library

### 07/11/14
__DONE__ fix yaml code and other changes for clojurescript

### 07/12/14
__DONE__ replace instances of format function with google closure format function
__DONE__ fix node bug
__DONE__ scrap node
__DONE__ convert file loading functions to use ajax requests

### 07/13/14
__DONE__ remove js-yaml
__DONE__ refactor file loading
__DONE__ write parse-file function
__DONE__ add json5
__DONE__ refactor json5 files

### 07/14/14
__DONE__ fix parse-file function
__DONE__ change json5 files to plain text
__DONE__ convert yaml files to json
__DONE__ fix get-lines-from-file
__DONE__ convert unit tests to specljs
__DONE__ add unit test for parse-file function
__DONE__ make rooms unit tests pass
__DONE__ setup source maps
__DONE__ replace java string functions with clojurescript equivalents
__DONE__ make parse-file unit test pass
__DONE__ refactor file loading
__DONE__ remove file-loading commented out code
__DONE__ rename ui stuff to state

### 07/15/14
__DONE__ bug fixes
__DONE__ setup ROT.js display

### 07/16/14
__DONE__ setup drawing with ROT.js
__DONE__ rewrite main loop to work with javascript
__DONE__ rewrite input handling to work with javascript
__DONE__ fix opening bug
__DONE__ write function to determine if a keycode is a character
__DONE__ convert libtcod code to use javascript library

### 07/18/14
__DONE__ rewrite load-house function
__DONE__ fix tile display
__DONE__ fix turn count bug
__DONE__ fix text entry bug
__DONE__ fix conservatory gformat bug

### 07/27/14
__DONE__ remove quitting options
__DONE__ center canvas in page
__DONE__ fix instructions

### 08/11/14
__DONE__ replace guest names

### 08/12/14
__DONE__ new room descriptions
__DONE__ new guest descriptions
__DONE__ new accuse text
__DONE__ new opening text