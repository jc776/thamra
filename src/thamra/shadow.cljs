(ns thamra.shadow
  (:require [thamra.shadow.model :as model]
            [thamra.shadow.view :as view]
            [thamra.shadow.effects :as fx]
            [thamra.dom :as d]
            [citrus.core :as citrus]))

(defonce reconciler
  (citrus/reconciler
    {:state (atom model/init-state)
     :controllers {:app #'model/handle-event}
     ;; how to make this one reloadable?
     :effect-handlers fx/effect-handlers}))

(def el (view/App {:r reconciler}))
