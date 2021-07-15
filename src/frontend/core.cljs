(ns frontend.core
  "This namespace contains your application and is the entrypoint for 'yarn start'."
  (:require [reagent.core :as r]
            [frontend.grid :as grid]))

(defn app []
  [:div.bg-yellow-300.p-12.h-screen
   [:h1.text-3xl.font-bold "MedMap"]
   [:div.mt-4
    [grid/component]]])

(defn ^:dev/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn ^:export main
  "Run application startup logic."
  []
  (render))
