(defproject sleuth "0.1.0-SNAPSHOT"
  :description "Clojure clone of the murder myster game Sleuth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [clj-yaml "0.4.0"]
                 [clj-native "0.9.3"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec"]
  :main sleuth.core
  )
