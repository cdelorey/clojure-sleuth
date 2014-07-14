(defproject sleuth "0.1.0-SNAPSHOT"
  :description "Clojure clone of the murder myster game Sleuth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
								 [domina "1.0.2"]
								 [cljs-ajax "0.2.6"]]
  :profiles {:dev {:dependencies [[specljs "2.9.1"]]}}
  :plugins [[specljs "2.9.1"]
            [lein-cljsbuild "1.0.3"]]
  :test-paths ["spec"]
  :main "resources/sleuth.js"

  :cljsbuild
  {:builds
   {:dev
    {:source-paths ["src/sleuth" "spec/sleuth"]
     :compiler
     {:output-to "resources/sleuth.js"
      :pretty-print true
      :externs ["externs.js"]}
		 :notify-command ["bin/speclj" "resources/sleuth.js"]}
		:prod
		{:source-paths ["src/sleuth"]
		 :compiler
		 {:output-to "resources/sleuth.js"
			:optimizations :simple}}}
	 :test-commands {"test" ["bin/speclj" "resources/sleuth.js"]}}
  )
