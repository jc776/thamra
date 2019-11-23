(ns ^:dev/always thamra.client
  (:require [thamra.demo :as demo]
            ["react-dom" :as react-dom]))

(defn run []
  (react-dom/render demo/el (js/document.getElementById "app")))

(defn ^:dev/before-load stop [])

(defn ^:dev/after-load start! []
  (run))

(defn init! [] (start!))