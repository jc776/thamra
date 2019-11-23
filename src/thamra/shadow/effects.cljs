(ns thamra.shadow.effects
  (:require [cognitect.transit :as transit]
            [citrus.core :as citrus]))

(def tool-host "localhost:9630")
(def tool-url (str "ws://" tool-host "/api/tool"))

(def msg-writer (transit/writer :json))
(def msg-reader (transit/reader :json))

(defn tool-message [rec ctrl e]
  (let [msg (transit/read msg-reader (.-data e))]
    (citrus/dispatch! rec ctrl :tool-message msg)))

(defn tool-send [rec ctrl {:keys [ws-state msg]}]
  (println "tool-send" msg ws-state)
  (if-let [socket (:socket ws-state)]
    (let [json (transit/write msg-writer msg)]
      (.send (:socket ws-state) json))
    (js/console.warn "tried to send before socket open")))

(defn tool-send-n [rec ctrl {:keys [ws-state msgs]}]
  (doseq [msg msgs]
    (tool-send rec ctrl {:ws-state ws-state :msg msg})))

(defn tool-connect [rec ctrl {:keys [ws-state]}]
  (let [socket (js/WebSocket. tool-url)]
    (citrus/dispatch! rec ctrl :ws-state
      {:socket socket})
    (.addEventListener socket "open"
      (fn [e] (citrus/dispatch! rec ctrl :tool-connected)))
    (.addEventListener socket "message"
      (fn [e] (tool-message rec ctrl e)))
    (.addEventListener socket "close"
      (fn [e] (js/console.log "tool-close" e)))
    (.addEventListener socket "error"
      (fn [e] (js/console.error "tool-error" e)))))

(defn dispatch [& args]
  (apply citrus/dispatch! args))

(def effect-handlers
  {:tool-connect tool-connect
   :tool-send tool-send
   :tool-send-n tool-send-n
   :dispatch dispatch})
