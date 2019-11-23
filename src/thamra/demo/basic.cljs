(ns thamra.demo.basic
  (:require [thamra.dom :as d]
            ["react" :as react]))

(d/defc Hello [{:keys [name]}]
  (d/h1 {} "Hello " name))

(d/defc Counter [{:keys [text]}]
  (let [[count setCount] (react/useState 0)]
    (d/div {}
      (d/button {:onClick #(setCount dec)} "-")
      text
      count
      (d/button {:onClick #(setCount inc)} "+"))))
