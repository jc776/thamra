(ns thamra.shadow.view
  (:require [thamra.dom :as d]
            [thamra.hook :as hook]
            [citrus.core :as citrus]))

(d/defc App [{:keys [r]}]
  (let [state (hook/useSub r [:app])]
    (hook/useEffect
      (fn []
        (citrus/dispatch! r :app :init)
        #(citrus/dispatch! r :app :unmount))
      #js [r])
    (d/div {}
      (d/pre {} (pr-str state))
      (d/pre {} (pr-str (:ws-state state)))
      (if (:ws-state state)
        (d/button {:onClick #(citrus/dispatch! r :app :tool-connected)} "Runtimes")
        (d/button {:onClick #(citrus/dispatch! r :app :init)} "Connect")))))
