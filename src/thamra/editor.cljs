(ns thamra.editor
  (:require [thamra.codemirror :refer [CodeMirror]]
            [thamra.react :as d]
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
	
(defn line-note [idx form]
  (d/div {:key idx} 
    (d/pre {:style {:display "inline-block" :color "red"}} (str (meta form))) 
	(d/pre {:style {:display "inline-block" :color "blue"}} (str form))))
	
(defn cljs-result-note [result]
  (let [{:keys [line column]} (meta result)]
    {:line line :ch column :text (str result)}))

(defn cljs-exception-note [ex]
  (let [{:keys [line col]} (ex-data ex)]
    {:line line :ch col :text (ex-message ex)}))
	
(defn cljs-notes [string]
  (try
    (mapv cljs-result-note (lined-read string :cljs))
	(catch js/Error ex
	  [(cljs-exception-note ex)]
	)))
	
;; todo: eval results 
;; todo: keep the objects around to inspect/use later
(defn result-line [idx res]
  (d/pre {:className "app-result" :key idx} (:text res)))

(d/defc Editor [{:keys [defaultValue]}]
  (let [[text setText] (react/useState defaultValue)]
  	(d/div {:className "app-container"}
	  (d/div {:className "app-editor"}
	    (CodeMirror {:defaultValue defaultValue :onChange setText}))
	  (d/div {:className "app-results"}
	    (map-indexed result-line (cljs-notes text) )))))
	  