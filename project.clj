(defproject dribbble-scraper "0.1.0-SNAPSHOT"
  :description "Scrapes dribbble.com/designers"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]]
  :plugins [[cider/cider-nrepl "0.12.0"]]
  :main ^:skip-aot dribbble-scraper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
