(ns user
  (:require [clojure.tools.deps.alpha.repl :refer [add-lib]]))

(println "user: load shadow.server")
(time (require '[shadow.cljs.devtools.server :as shadow.server]))

(println "user: load shadow")
(time (require '[shadow.cljs.devtools.api :as shadow]))

(println "user: start shadow.server")
(time (shadow.server/start!))

(println "user: ready")

(defn start []
  (shadow/watch :app))