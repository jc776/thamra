(ns repl
  (:require [rebel-readline.main]))

(defn start-rebel []
  (rebel-readline.main/-main))

(defn -main [& args]
  (start-rebel)
  (System/exit 0))