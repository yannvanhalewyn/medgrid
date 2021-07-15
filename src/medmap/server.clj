(ns medmap.server
  (:require [org.httpkit.server :as server]
            [ring.middleware.resource :as ring.resource]
            [medmap.endpoints :as endpoints]
            [muuntaja.core :as m]
            [muuntaja.middleware :as m.middleware]))


(defonce server (atom nil))

(def muuntaja
  (m/create
   (update-in
    (assoc m/default-options :default-format "application/transit+json")
    [:formats "application/transit+json"]
    merge )))

(defn- routes [req]
  (case (:uri req)
    "/" (endpoints/index req)
    "/api/grid" (endpoints/grid req)
    (endpoints/not-found req)))

(defn- make-handler [routes]
  (-> routes
      (ring.middleware.resource/wrap-resource "public")
      (m.middleware/wrap-format muuntaja)))

(defn start! []
  (if @server
    (println "Server already running")
    (reset! server (server/run-server (make-handler routes) {:port 8080}))))

(defn stop! []
  (if @server
    (do (@server)
        (reset! server nil))
    (println "Server not running")))
