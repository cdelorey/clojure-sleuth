(ns sleuth.utils-spec
	(:require [specljs.core]
            [sleuth.utils :refer [parse-file]])
	(:require-macros [specljs.core :refer [describe it with should=]]))

(describe "parse-file"
					(with text "*victim* was brutally murdered only hours ago.\nThe body was removed by persons unknown from the murder scene.\nYou are now standing at the front door of the *victim-last* estate.\nThe door is open.")

					(it "returns a valid clojure object"
							(should= @text (:opening (parse-file "http://localhost:8000/json5/opening.txt")))))
