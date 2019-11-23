(ns thamra.demo
  (:require [thamra.demo.basic :as b]
            [thamra.demo.citrus-counter :as ct]
            [thamra.dom :as d]
            [cljs.repl :as repl]
            [citrus.core :as citrus]))

(defn fact
  ([n] (fact n 1))
  ([n f]
   (if (<= n 1)
     f
     (recur (dec n) (* f n)))))

(def initial-src
  (str (with-out-str (repl/source fact))
       "\n"
       "(fact 5)"))

;; create Reconciler instance
(defonce reconciler
  (citrus/reconciler
    {:state
     (atom {}) ;; application state
     :controllers
     {:counter ct/control} ;; controllers
     :effect-handlers
     {:local-storage ct/local-storage}})) ;; effect handlers

;; initialize controllers
(defonce init-ctrl (citrus/broadcast-sync! reconciler :init))

(def el
  (d/<> {}
    (b/Hello {:key "hello" :name "world!"})

    (b/Counter {:key "count" :text "Count: "})

    (ct/AppCounter {:r reconciler})

    (d/div {:key "text" :style {"fontWeight" "bold"}} "Hello " "there!")

    (d/pre {} initial-src)

    #_(editor/Editor {:key "editor" :defaultValue initial-src})))
