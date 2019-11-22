(ns thamra.app
  (:require [thamra.hook :as hook]
            [thamra.dom :as d]
            [citrus.core :as citrus]))

(def initial-state 0)

;; do I like multimethods?
(defmulti control (fn [event] event))

(defmethod control :init []
  {:local-storage
   {:method :get
    :key :counter
    :on-read :init-ready}}) ;; read from local storage

(defmethod control :init-ready [_ [counter]]
  (println "init-ready" counter)
  (if-not (nil? counter)
    {:state (js/parseInt counter)} ;; init with saved state
    {:state initial-state})) ;; or with predefined initial state

(defmethod control :inc [_ _ counter]
  (let [next-counter (inc counter)]
    {:state next-counter ;; update state
     :local-storage
     {:method :set
      :data next-counter
      :key :counter}})) ;; persist to local storage

(defmethod control :dec [_ _ counter]
  (let [next-counter (dec counter)]
    {:state next-counter ;; update state
     :local-storage
     {:method :set
      :data next-counter
      :key :counter}})) ;; persist to local storage
	  
;; effects

(defn local-storage [reconciler controller-name effect]
  (let [{:keys [method data key on-read]} effect]
    (case method
      :set (js/localStorage.setItem (name key) data)
      :get (->> (js/localStorage.getItem (name key))
                (citrus/dispatch! reconciler controller-name on-read))
      nil)))
	  
;; view

(d/defc AppCounter [{:keys [r]}]
  (let [count (hook/useSub r [:counter])]
    (d/div {}
      (d/button {:onClick #(citrus/dispatch! r :counter :dec)} "-")
      (d/span {} count)
      (d/button {:onClick #(citrus/dispatch! r :counter :inc)} "+"))))