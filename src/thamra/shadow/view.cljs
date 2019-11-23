(ns thamra.shadow.view
  (:require [thamra.dom :as d]
            [thamra.hook :as hook]
            [citrus.core :as citrus]))

(d/defc App [{:keys [r]}]
  (let [state (hook/useSub r [])]
    (hook/useEffect
      (fn []
        (citrus/dispatch! r :app :init)
        #(citrus/dispatch! r :app :unmount))
      #js [r])
    (d/div {}
      (d/pre {} (pr-str state))
      (d/button {:onClick #(citrus/dispatch! r :app :connect)} "Connect"))))
