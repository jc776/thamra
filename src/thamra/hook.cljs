(ns thamra.hook
  (:require ["react" :as react]
            [citrus.core :as citrus]))

(defn useState
  [initial-value]
  (react/useState initial-value))

(defn useEffect
  ([f]
   (react/useEffect f))
  ([f deps]
   (react/useEffect f (to-array deps))))

(defn useValue
  "Caches `x`. When a new `x` is passed in, returns new `x` only if it is
  not structurally equal to the previous `x`.
  Useful for optimizing `useEffect` et. al. when you have two values that might
  be structurally equal by referentially different."
  [x]
  (let [-x (react/useRef x)]
    ;; if they are equal, return the prev one to ensure ref equality
    (let [x' (if (= x (.-current -x))
               (.-current -x)
               x)]
      ;; Set the ref to be the last value that was succesfully used to render
      (react/useEffect (fn []
                         (set! (.-current -x) x)
                         js/undefined)
                       #js [x'])
      x')))

(defn useAtom
  [a]
  (let [current-val @a
        [val set-val] (useState current-val)]
    (useEffect
      (fn []
        (println "effect" a)
        (let [id (str (random-uuid))]
          (add-watch a id
            (fn [_ _ _ new-state]
              (set-val new-state)))
          ;; updated between first render and this effect
          (when (not= @a val)
            (set-val @a))
          #(remove-watch a id)))
      [a])
    current-val))

(defn subscribe! [args]
  (println "sub" args)
  (apply citrus.core/subscription args))

(def subscribe (memoize subscribe!))

(defn useSub [& args]
  (let [current-sub (subscribe args)]
    (useAtom current-sub)))
