(ns thamra.shadow.view
  (:require [thamra.dom :as d]
            [thamra.hook :as hook]
            [thamra.codemirror :refer [CodeMirror]]
            [cljs.pprint]
            [citrus.core :as citrus]))


;; TODO: use the "writer" version
(defn pprint-str [x]
  (with-out-str (cljs.pprint/pprint x)))

(def eval-ops
  {:eval-clj "Clojure"
   :eval-cljs "ClojureScript"
   :eval-js "Javascript"})

(d/defc RuntimeInfo [{:keys [r info]}]
  (let [{:keys [rid lang since build-id user-agent ops]} info]
    (d/tr {}
      (d/td {} (str rid))
      (d/td {} (str since))
      (d/td {} (str lang))
      (d/td {} (str build-id))
      (d/td {} (str user-agent))
      (d/td {}
        (for [[op name] eval-ops]
          (when (op ops)
            (d/button {:key op :onClick #(citrus/dispatch! r :app :workspace-open {:rid rid :op op})} name)))))))

(d/defc RefResult [{:keys [r rid oid state]}]
  ;; or... on hover?
  ;; if not already in the state?
  (hook/useEffect
    (fn []
      (citrus/dispatch! r :app :obj-request rid oid :edn)
      js/undefined)
    #js [r rid oid])
  (if-let [result (get-in state [:oid oid :result])]
    (d/pre {:className "workspace-result"} result)
    "Loading..."))

;; number: :get-value :summary :edn :edn-limit :pprint
;; map:     :fragment :summary :edn :edn-limit :pprint :nav

(defn show-result [r rid result state]
  (cond
    (nil? result)
    "Eval with Shift+Enter"

    (:ref-oid result)
    (RefResult {:r r :rid rid :oid (:ref-oid result) :state state})

    :else
    "Unknown result type"))

(d/defc Workspace [{:keys [r info state]}]
  (let [{:keys [rid op id results]} info]
    (d/div {:className "workspace"}
      (d/button {:onClick #(citrus/dispatch! r :app :workspace-close id)} "Close")
      (pr-str {:id id :op op :rid rid})
      (d/div {:className "workspace-container"}
        (d/div {:className "workspace-editor"}
          (CodeMirror {:onEval #(citrus/dispatch! r :app :workspace-eval id (.getValue %))}))
        (d/div {:className "workspace-results"}
          (show-result r rid results state))))))

(defn segments-ordered [notebook]
  (let [segments (:segments notebook)
        segment-ids-ordered (:segment-order notebook)]
    (vec (map #(get segments %) segment-ids-ordered))))

(d/defc App [{:keys [r]}]
  (let [state (hook/useSub r [:app])]
    (hook/useEffect
      (fn []
        (citrus/dispatch! r :app :init)
        #(citrus/dispatch! r :app :unmount))
      #js [r])
    (d/div {}
      (d/h1 {} "Available Runtimes")
      (d/table {}
        (d/tbody {}
          (for [[rid info] (:runtimes state)]
            (RuntimeInfo {:key rid :r r :info info}))))
      (for [{:keys [id] :as info} (segments-ordered (:workspaces state))]
        (Workspace {:key id :r r :info info :state state}))
      (d/pre {} (pprint-str state))
      (if (:ws-state state)
        (d/button {:onClick #(citrus/dispatch! r :app :tool-connected)} "Runtimes")
        (d/button {:onClick #(citrus/dispatch! r :app :init)} "Connect")))))
