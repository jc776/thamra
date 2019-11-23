(ns thamra.shadow.model)

(def init-state
  {})

(defn merge-key [m k next]
  (assoc m k (merge (k m) next)))

(declare handle-tool-message)

;; app events

(defmulti handle-event
  (fn [event args]
    (println "event" event args)
    event))

(defmethod handle-event :init [_ _ {:keys [ws-state]}]
  {:tool-connect {:ws-state ws-state}})

(defmethod handle-event :unmount []
  (println "unmount"))

(defmethod handle-event :ws-state [_ [next] state]
  {:state (merge-key state :ws-state next)})

(defmethod handle-event :tool-connected [_ _ {:keys [ws-state]}]
  ;; or, "dispatch request-runtimes"
  {:tool-send {:ws-state ws-state
               :msg {:op :request-runtimes}}})

(defmethod handle-event :tool-message [_ [msg] state]
  (handle-tool-message msg state))

(defmethod handle-event :default [event args]
  (println "unknown event" event args))

;; responses from shadow.remote

(defmulti handle-tool-message (fn [msg] (:op msg)))

(defn runtimes-map [runtimes]
  (->> runtimes
       (map (fn [{:keys [rid] :as info}] [rid info]))
       (into {})))

(defn request-supported-ops [{:keys [rid]}]
  {:op :request-supported-ops :rid rid})

(defmethod handle-tool-message :runtimes [{:keys [runtimes]} {:keys [ws-state] :as state}]
  {:state (assoc state :runtimes (runtimes-map runtimes))
   :tool-send-n {:ws-state ws-state :msgs (map request-supported-ops runtimes)}})

(defmethod handle-tool-message :runtime-connect [{:keys [rid] :as info} {:keys [ws-state] :as state}]
  {:state (assoc-in state [:runtimes rid] info)
   :tool-send {:ws-state ws-state :msg (request-supported-ops info)}})

(defmethod handle-tool-message :runtime-disconnect [{:keys [rid]} state]
  {:state (assoc-in state [:runtimes rid] :dead)})

(defmethod handle-tool-message :supported-ops [{:keys [ops rid]} state]
  {:state (assoc-in state [:runtimes rid :ops] ops)})

(defmethod handle-tool-message :default [msg]
  (println "unknown tool message" msg))
