(ns thamra.shadow.view
  (:require [thamra.dom :as d]
            [thamra.hook :as hook]
            [thamra.codemirror :as codemirror]
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
            (d/button {:key op :onClick #(citrus/dispatch! r :app :open-workspace {:rid rid :op op})} name)))))))

(d/defc Workspace [{:keys [r info]}]
  (let [{:keys [id]} info]
    (d/div {:className "workspace"}
      (d/button {:onClick #(citrus/dispatch! r :app :close-workspace id)} "Close")
      (d/pre {} "Workspace: " (pr-str info)))))

(d/defc App [{:keys [r]}]
  (let [state (hook/useSub r [:app])]
    (println "view called")
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
      (for [{:keys [id] :as info} (:workspaces state)]
        (Workspace {:key id :r r :info info}))
      (d/pre {} (pprint-str state))
      (if (:ws-state state)
        (d/button {:onClick #(citrus/dispatch! r :app :tool-connected)} "Runtimes")
        (d/button {:onClick #(citrus/dispatch! r :app :init)} "Connect")))))
