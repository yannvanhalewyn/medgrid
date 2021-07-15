(ns frontend.db
  (:require [reagent.core :as r]
            [reagent.ratom :as ratom]
            [ajax.core :as ajax]))

(defonce db (r/atom {}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Remote subscriptions

(defn- fetch [params handler]
  (ajax/GET (str "/api" (:path params))
            {:handler handler
             :error-handler #(.error js/console "Error fetching data.")
             :format :transit
             :response-format :transit}))

(defn- make-remote-subscription [key params]
  (ratom/reaction
   (let [data (get @db key)]
     (when (nil? data)
       (fetch params #(swap! db assoc key %)))
     data)))

(def remote-grid-data
  (make-remote-subscription ::grid-data {:path "/grid"}))
