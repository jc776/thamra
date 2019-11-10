(ns thamra.client
  (:require [cljs.repl :as repl]
            [thamra.editor :as editor]
            [thamra.react :as d]
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
    #_(Hello {:key "hello" :name "world!"})
	
	#_(Counter {:key "count" :text "Count: "})
	
	#_(d/div {:key "text" :style {"fontWeight" "bold"}} "Hello " "there!")
	
	(editor/Editor {:key "editor" :defaultValue initial-src})
	
  ))

(js/console.log "load client" el)


(react-dom/render el (js/document.getElementById "app"))