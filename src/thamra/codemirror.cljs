(ns thamra.codemirror
  (:require [thamra.dom :as d]
            ["react" :as react]
            ["codemirror" :as codemirror]
            ["codemirror/mode/clojure/clojure"] ;; side effect
            ["codemirror/mode/javascript/javascript"] ;; side effect
            ["parinfer-codemirror" :as par-cm]))

(def codemirror-opts
  {:mode "clojure"
   :lineNumbers true
   :viewportMargin js/Infinity})

(d/defc CodeMirror [{:keys [defaultValue onChange onEval]}]
  (let [inputRef (react/useRef)]
    (react/useEffect
      (fn []
        (let [dom-node (.-current inputRef)
              opts (clj->js codemirror-opts)
              cm (codemirror/fromTextArea dom-node opts)]
         (when onChange
           (.on cm "change" #(onChange (.getValue cm))))
         (when onEval
           (.setOption cm "extraKeys"
             #js {"Ctrl-Enter" #(onEval cm)
                  "Shift-Enter" #(onEval cm)}))
         (par-cm/init cm)
         ;; cleanup? unmount?
         js/undefined))
      #js [])
    (d/textarea {:ref inputRef :defaultValue defaultValue})))
