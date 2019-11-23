(ns thamra.dom)

(defn fnc* [display-name props-bindings body]
  `(fn ~display-name [props# maybe-ref#]
     (let [~props-bindings [(thamra.dom/props->clj props#) maybe-ref#]]
       (do ~@body))))

(defmacro fnc [display-name props-bindings & body]
  (fnc* display-name props-bindings body))

;; A-impl - react function component
;; A-wrap - stable reference for hot-loading (todo: debug only?)
;; A - react element factory

;; I'd prefer #'A instead of wrap to match (start-server #'routes) at the repl
;; but the var doesn't count as a function component for React

(defmacro defc [tag props-bindings & body]
  (let [tag-impl (symbol (str tag "Impl"))
        tag-wrap (symbol (str tag "Wrap"))]
    `(do (def ~tag-impl ~(fnc* tag props-bindings body))
       (defonce ~tag-wrap (fn [& args#] (apply ~tag-impl args#)))
      (defn ~tag [& args2#]
              (apply h ~tag-wrap args2#))
      (goog.object/set ~tag-impl "displayName" ~(str *ns* "/" tag)))))

(defn tag-definition [tag]
  `(defn ~tag [& args#]
     (apply h ~(name tag) args#)))

(defmacro define-tags [& tags]
  `(do ~@(clojure.core/map tag-definition tags)))
