(ns thamra.editor
  (:require [thamra.react :as d]
            ["react" :as react]
			[cljsjs.codemirror.mode.clojure] ;; require for side effect?
			["codemirror" :as codemirror]
			["parinfer-codemirror" :as par-cm]))

(def codemirror-opts
  #js {:mode "clojure"
       :lineNumbers true})

(d/defc CodeMirror [{:keys [onChange]}]
  (let [inputRef (react/useRef)]
    (react/useEffect 
	  (fn [] 
	     (let [dom-node (.-current inputRef)
		       cm (codemirror/fromTextArea dom-node codemirror-opts)]
		   (par-cm/init cm)
		   (.on cm "change" #(onChange (.getValue cm)))
		   ;; any leak?
	       js/undefined))
      #js [])
    (d/textarea {:ref inputRef})))
	

(d/defc Editor []
  (let [[text setText] (react/useState "")]
    (d/div {}
      (CodeMirror {:onChange setText})
      (d/pre {} text))))