(ns thamra.codemirror
  (:require [thamra.react :as d]
            ["react" :as react]
            [cljsjs.codemirror.mode.clojure] ;; side effect
            ["codemirror" :as codemirror]
            ["parinfer-codemirror" :as par-cm]))

(def codemirror-opts
  {:mode "clojure"
   :lineNumbers true})

(d/defc CodeMirror [{:keys [defaultValue onChange onInit]}]
  (let [inputRef (react/useRef)]
    (react/useEffect 
	  (fn [] 
	     (let [dom-node (.-current inputRef)
		       opts (clj->js codemirror-opts)
		       cm (codemirror/fromTextArea dom-node opts)]
		   (par-cm/init cm)
		   (.on cm "change" #(onChange (.getValue cm)))
		   (when onInit (onInit cm))
		   ;; any leak?
	       js/undefined))
      #js [])
    (d/textarea {:ref inputRef :defaultValue defaultValue})))