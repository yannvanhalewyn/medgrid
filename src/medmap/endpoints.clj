(ns medmap.endpoints
  (:require [hiccup.page :as hp]
            [medmap.grid :as grid]
            [medmap.command :as command]
            [clojure.java.io :as io]))

(defn- html-response [html-str]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body html-str})

(defn index [_req]
  (html-response
   (hp/html5
    [:head
     [:title "MedMap"]
     (hp/include-css "https://unpkg.com/tailwindcss@%5E2/dist/tailwind.min.css")]
    [:body
     [:div#app]
     (hp/include-js "/js/main.js")])))

(defn not-found [req]
  {:status 404
   :body (str "NOT FOUND: " (:uri req))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; API

(def WIDTH 700)
(def HEIGHT 700)

(def grid-data
  (delay
    (let [grid (grid/make WIDTH HEIGHT)]
      (with-open [rdr (io/reader (io/resource "input-data.txt"))]
        (->> (command/parse-lines rdr)
             (command/apply-commands grid)
             ::grid/cells
             count)))))

(defn grid [_req]
  {:status 200
   :body @grid-data})
