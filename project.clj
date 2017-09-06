(defproject amoo-babak "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [http-kit "2.2.0"]
                 [compojure "1.6.0"]
                 [hickory "0.7.1"]
                 [cheshire "5.8.0"]
                 [datascript "0.16.2"]
                 [proto-repl "0.3.1"]]
  :main ^:skip-aot amoo-babak.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
