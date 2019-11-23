(ns thamra.shadow.model
  (:require [thamra.util :as util]))

(def init-state
  {:workspaces {:segments {}
                :segment-order []}})

(declare handle-tool-message)

;; app events

(defmulti handle-event
  (fn [event args state]
    (println "event:" event args)
    event))

(defmethod handle-event :init [_ _ {:keys [ws-state]}]
  {:tool-connect {:ws-state ws-state}})

(defmethod handle-event :unmount []
  (println "unmount"))

(defmethod handle-event :ws-state [_ [next] state]
  (println "add to ws-state" next)
  {:state (util/merge-key state :ws-state next)})

(defmethod handle-event :tool-connected [_ _ {:keys [ws-state]}]
  ;; or, "dispatch request-runtimes"
  {:tool-send {:ws-state ws-state
               :msg {:op :request-runtimes}}})

(defn clear-reply-info [prev-state mid {:keys [state] :as result}]
  (let [state (or state prev-state)
        state (util/dissoc-in state [:ws-state :reply-info mid])]
    (assoc result :state state)))

(defmethod handle-event :tool-message [_ [{:keys [mid] :as msg}] state]
  (if-let [info (and mid (get-in state [:ws-state :reply-info mid]))]
    (clear-reply-info state mid (handle-tool-message msg state info))
    (handle-tool-message msg state)))

(defn insert-segment-at
  [worksheet new-index new-segment]
  (let [segment-order (:segment-order worksheet)
        segments (:segments worksheet)
        new-id (:id new-segment)
        [head tail] (split-at new-index segment-order)]
    (merge worksheet {:active-segment new-id
                      :segments       (assoc segments new-id new-segment)
                      :segment-order  (into [] (concat head (conj tail new-id)))})))

(defn remove-segment
  [worksheet seg-id]
  (let [segment-order (:segment-order worksheet)
        active-id (:active-segment worksheet)
        seg-idx (.indexOf segment-order seg-id)
        next-active-idx (if (and (= active-id seg-id) (> seg-idx 0))
                          (nth segment-order (- seg-idx 1)))
        segments (:segments worksheet)]
    (merge worksheet {:active-segment next-active-idx
                      :segments       (dissoc segments seg-id)
                      :segment-order  (into [] (remove #(= seg-id %) segment-order))})))

(defmethod handle-event :workspace-open [_ [info] state]
  (let [id (str (random-uuid))
        new-segment (assoc info :id id)]
    {:state (update state :workspaces insert-segment-at 0 new-segment)}))

(defmethod handle-event :workspace-close [_ [id] state]
  {:state (update state :workspaces remove-segment id)})

(defn tool-call [info state msg]
  (let [ws-state (:ws-state state)
        next-msg-id (inc (:msg-id ws-state))
        ws-state (assoc ws-state :msg-id next-msg-id)
        ws-state (assoc-in ws-state [:reply-info next-msg-id] info)
        next-state (assoc state :ws-state ws-state)
        call-msg (assoc msg :mid next-msg-id)]
    {:state next-state
     :tool-send {:ws-state ws-state
                 :msg call-msg}}))

(defmethod handle-event :workspace-eval [_ [id code] {:keys [workspaces] :as state}]
  (let [{:keys [rid op]} (get-in workspaces [:segments id])]
    (tool-call {:id id}
      state
      {:op op
       :rid rid
       :code code})))

(defmethod handle-event :obj-describe [_ [rid oid] {:keys [ws-state]}]
  {:tool-send {:ws-state ws-state
               :msg {:op :obj-describe
                     :rid rid
                     :oid oid}}})

(defmethod handle-event :obj-request [_ [rid oid request-op] {:keys [ws-state]}]
  {:tool-send {:ws-state ws-state
               :msg {:op :obj-request
                     :rid rid
                     :oid oid
                     :request-op request-op}}})

(defmethod handle-event :default [event args]
  (println "unknown event" event args))

;; responses from shadow.remote

(defmulti handle-tool-message (fn [msg] (:op msg)))

(defn active-runtime [info]
  (assoc info :runtime-active true))

(defn runtimes-map [runtimes]
  (->> runtimes
       (map (fn [{:keys [rid] :as info}] [rid (active-runtime info)]))
       (into {})))

(defn request-supported-ops [{:keys [rid]}]
  {:op :request-supported-ops :rid rid})

;; TODO: runtime-active

(defmethod handle-tool-message :runtimes [{:keys [runtimes]} {:keys [ws-state] :as state}]
  {:state (assoc state :runtimes (runtimes-map runtimes))
   :tool-send-n {:ws-state ws-state :msgs (map request-supported-ops runtimes)}})

(defmethod handle-tool-message :runtime-connect [{:keys [rid runtime-info]} {:keys [ws-state] :as state}]
  {:state (assoc-in state [:runtimes rid] (active-runtime runtime-info))
   :tool-send {:ws-state ws-state :msg (request-supported-ops runtime-info)}})

(defmethod handle-tool-message :runtime-disconnect [{:keys [rid]} state]
  {:state (util/dissoc-in state [:runtimes rid])})

(defmethod handle-tool-message :supported-ops [{:keys [ops rid]} state]
  {:state (assoc-in state [:runtimes rid :ops] ops)})

(defmethod handle-tool-message :eval-result-ref [{:keys [ref-oid]} state {workspace-id :id}]
  {:state (assoc-in state [:workspaces :segments workspace-id :results] {:ref-oid ref-oid})})

(defmethod handle-tool-message :eval-error [{:keys [e]} state {workspace-id :id}]
  {:state (assoc-in state [:workspaces :segments workspace-id :results] {:error e})})

(defmethod handle-tool-message :obj-summary [{:keys [oid summary]} state]
  {:state (assoc-in state [:oid oid :summary] summary)})

(defmethod handle-tool-message :obj-result [{:keys [oid result]} state]
  ;; needs request info to know what format this is
  {:state (assoc-in state [:oid oid :result] result)})

(defmethod handle-tool-message :default [msg]
  (println "unknown tool message" msg))
