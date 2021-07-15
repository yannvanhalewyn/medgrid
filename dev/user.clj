(ns user
  (:require [medmap.server :as server]))

(defn go []
  (server/start!))


(defn halt []
  (server/stop!))

(defn reset []
  (server/stop!)
  (server/start!))
