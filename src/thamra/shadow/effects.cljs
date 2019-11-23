(ns thamra.shadow.effects
  (:require [cognitect.transit :as transit]
            [citrus.core :as citrus]))

(def tool-host "localhost:9630")
(def tool-url (str "ws://" tool-host "/api/tool"))

(defn websocket-message [rec ctrl e]
  (let [t (transit/reader :json)
        {:keys [op mid] :as msg} (transit/read t (.-data e))]
    (citrus/dispatch! rec ctrl :tool-message msg)))

(defn websocket-connect [rec ctrl {:keys [ws-state]}]
  (let [socket (js/WebSocket. tool-url)]
    (citrus/dispatch! rec ctrl :ws-state
      (assoc ws-state :socket socket))
    (.addEventListener socket "open"
      (fn [e] (citrus/dispatch! rec ctrl :tool-connected)))
    (.addEventListener socket "message"
      (fn [e] (websocket-message rec ctrl e)))
    (.addEventListener socket "close"
      (fn [e] (js/console.log "tool-close" e)))
    (.addEventListener socket "error"
      (fn [e] (js/console.error "tool-error" e)))))


(def effect-handlers
  {:ws-connect websocket-connect})
