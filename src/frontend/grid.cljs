(ns frontend.grid
  (:require [frontend.db :as db]))


(defn component []
  (let [grid @db/remote-grid-data]

    (.log js/console grid)
    [:div "GRID"]))
