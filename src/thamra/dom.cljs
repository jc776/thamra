(ns thamra.dom
  (:refer-clojure :exclude [time map meta mask])
  (:require ["react" :as react]
            [cljs-bean.core :as bean]
            [goog.object :as gobj])
  (:require-macros [thamra.dom :as d]))

;; todo: "optional props"
;; todo: defn vs macro for hyperscript
;; ignoring: "compile time props"

(def props->clj bean/bean)

(defn clj->props [input]
  ;; when bean? use .obj ...
  (let [m (js-obj)]
    (doseq [[k v] input]
      (gobj/set m (name k) v))
    m))

(defn h [type props & children]
  (apply react/createElement type (clj->props props) children))

(defn <> [props & children]
  (apply h react/Fragment props children))

(d/define-tags
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
