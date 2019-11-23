(ns thamra.shadow.model)

(def init-state
  {:test 1})

(defn unknown-event [event args]
  (println "unknown event" event args))

(defn handle-event [event args {:keys [ws-state] :as state}]
  (println "event" event args)
  (case event
    :init
    {:ws-connect {:ws-state ws-state}}

    :unmount
    (println "unmount")

    :ws-state
    {:state (assoc state :ws-state args)}

    (unknown-event event args)))
