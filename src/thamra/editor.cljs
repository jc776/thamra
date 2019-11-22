(ns thamra.editor
  (:require [thamra.codemirror :refer [CodeMirror]]
            [thamra.react :as d]
            [eval-soup.core :as es]
            [clojure.tools.reader :as reader]
            [clojure.tools.reader.reader-types :as rt]
            ["react" :as react]))
  
(defn read-one [rdr feature]
  {:pre [(#{:clj :cljs} feature)]}
  (when rdr
    (reader/read {:read-cond :allow :features #{feature} :eof ::EOF} rdr)))

(defn lined-read
  ([string] (lined-read string :clj))
  ([string feature]
   (let [rdr (rt/indexing-push-back-reader string)]
     (take-while #(not= ::EOF %) (repeatedly #(read-one rdr feature))))))
	
(defn cljs-result-note [result]
  (let [{:keys [line column]} (meta result)]
    {:line line :ch column :text (str result)}))

(defn cljs-exception-note [ex]
  (let [{:keys [line col]} (ex-data ex)]
    {:line line :ch col :text (ex-message ex)}))
	

;; todo: don't continue past reader errors https://github.com/nrepl/nrepl/pull/107
;; todo: don't continue past eval errors
(defn cljs-notes [code cb]
  (try
    (es/code->results (lined-read code :cljs) cb)
	(catch js/Error ex
	  (cb [(ex-message ex)])
	)))
	
;; todo: eval results 
;; todo: keep the objects around to inspect/use later
(defn result-line [idx res]
  (d/pre {:className "app-result" :key idx} (str res)))

(d/defc Editor [{:keys [defaultValue]}]
  (let [[text setText] (react/useState defaultValue)
        [results setResults] (react/useState [])]
	(react/useEffect
	  (fn []
	    (cljs-notes text setResults)
	    js/undefined)
	  #js [text])
  	(d/div {:className "app-container"}
	  (d/div {:className "app-editor"}
	    (CodeMirror {:defaultValue defaultValue :onChange setText}))
	  (d/div {:className "app-results"}
	    (map-indexed result-line results )))))
	  