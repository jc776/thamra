(ns thamra.react
  (:refer-clojure :exclude [time map meta mask])
  (:require ["react" :as react]
            [goog.object :as gobj])
  (:require-macros [thamra.react :as tr]))
  
;; todo: "optional props"
;; todo: defn vs macro for hyperscript
;; ignoring: "compile time props"
;; do I like "shallow convert"? how useful is that for your components vs js ones?
  
(defn set-obj [o k v]
  (do (gobj/set o k v)
      o))
  
(defn clj->props [props]
  (loop [pxs (seq props)
          js-props #js {}]
     (if (nil? pxs)
       js-props
	   (let [p (first pxs)
             k (key p)
             v (val p)]
	     (set-obj js-props (name k) v)
	     (recur (next pxs)
                js-props)))))

(defn h [type props & children]
  (apply react/createElement type (clj->props props) children))

;; is there a better "cljs bean" one of these?
(defn props->clj [props]
  (loop [ks (js/Object.keys props)
         m {}]
    (if (nil? ks)
      m
      (let [k (first ks)
            v (gobj/get props k)]
        (recur (next ks)
               (assoc m (keyword k) v))))))
			   
(tr/define-tags
  a abbr address area article aside audio b base bdi bdo big blockquote body br
  button canvas caption cite code col colgroup data datalist dd del details dfn
  div dl dt em embed fieldset figcaption figure footer form h1 h2 h3 h4 h5 h6
  head header hr html i iframe img input ins kbd keygen label legend li link main
  map mark menu menuitem meta meter nav noscript object ol optgroup option output
  p param pre progress q rp rt ruby s samp script section select small source
  span strong style sub summary sup table tbody td textarea tfoot th thead time
  title tr track u ul var video wbr circle clipPath defs ellipse g image line
  linearGradient mask path pattern polygon polyline radialGradient rect stop svg
  text tspan)