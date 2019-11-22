(ns ^:dev/always thamra.client
  (:require [cljs.repl :as repl]
            #_[thamra.editor :as editor]
            [thamra.dom :as d]
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
	
(defn fact
  ([n] (fact n 1))
  ([n f]
  (if (<= n 1)
    f
    (recur (dec n) (* f n)))))

(def initial-src 
  (str (with-out-str (repl/source fact))
       "\n"
	   "(fact 5)"))

(def el 
  (d/<> {}
    (Hello {:key "hello" :name "world!"})
	
	(Counter {:key "count" :text "Count: "})
	
	(d/div {:key "text" :style {"fontWeight" "bold"}} "Hello " "there!")
	
	(d/pre {} initial-src)
	
	#_(editor/Editor {:key "editor" :defaultValue initial-src})
	
  ))

(defn run []
  (js/console.log "hello" (js/Date.))
  (react-dom/render el (js/document.getElementById "app")))

(defn ^:dev/before-load stop []
  (js/console.log "stop"))

(defn ^:dev/after-load start! []
  (run))

(defn init! [] (start!))