(defproject clojure-rss-reader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [duct/core "0.8.0"]
                 [metosin/ring-http-response "0.9.4"]
                 [duct/module.ataraxy "0.3.0"]
                 [duct/module.logging "0.5.0"]
                 [duct/module.web "0.7.4"]
                 [duct/database.sql.hikaricp "0.4.0"]
                 [org.postgresql/postgresql "42.7.4"]
                 [com.github.seancorfield/honeysql "2.6.1243"]
                 [com.github.seancorfield/next.jdbc "1.3.981"]
                 [clj-http "3.13.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.zip "1.1.0"]]
  :plugins [[duct/lein-duct "0.12.3"]]
  :main ^:skip-aot clojure-rss-reader.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [hawk "0.2.11"]
                                   [eftest "0.5.9"]
                                   [kerodon "0.9.1"]]}})
