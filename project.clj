(defproject sleuth "0.1.0-SNAPSHOT"
  :description "Clojure clone of the murder myster game Sleuth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [domina "1.0.2-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :plugins [[speclj "2.5.0"]
            [lein-cljsbuild "0.3.2"]
            [lein-npm "0.4.0"]]
  :node-dependencies [[js-yaml "3.0.2"]]
  :test-paths ["spec"]
  :main sleuth.core

  :cljsbuild
  {:builds
   {:sleuth
    {:source-paths ["src/sleuth"]
     :compiler
     {:output-to "resources/sleuth.js"
      :optimizations :whitespace
      :pretty-print true}}}}
  )
