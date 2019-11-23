(ns ^:dev/always thamra.client
  (:require [thamra.shadow :as shadow]
            ["react-dom" :as react-dom]))

(defn run []
  (react-dom/render shadow/el (js/document.getElementById "app")))

(defn ^:dev/before-load stop [])

(defn ^:dev/after-load start! []
  (run))

(defn init! [] (start!))
