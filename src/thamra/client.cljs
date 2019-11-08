(ns thamra.client
  (:require [thamra.react :as d]
            ["react" :as react]
            ["react-dom" :as react-dom]))

(d/defc Hello [{:keys [name]}]
  (d/h1 {} "Hello " name))
  
(d/defc Counter [{:keys [text]}]
  (let [[count setCount] (react/useState 0)]
    (d/div {} 
	  (d/button {:onClick #(setCount dec)} "-")
	  text
	  count
	  (d/button {:onClick #(setCount inc)} "+")
	
	)))

(def el 
  #_(d/div {:style #js {"fontWeight" "bold"}} "Hello " "there!")
  
  #_(Hello {:name "world!"})
  
  (Counter {:text "Count: "})
  
  )

(js/console.log "load client" el)


(react-dom/render el (js/document.getElementById "app"))