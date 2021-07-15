(ns frontend.grid
  (:require [frontend.db :as db]
            [medmap.grid :as grid]))

(defn- ->pixels [{::grid/keys [width height] :as grid}]
  (for [x (range width)
        y (range height)]
    (if (grid/active? grid [x y])
      #js [239 68 68 255]
      #js [255 255 255 255])))

(defn- image-data [pixels width height]
  (js/ImageData. (js/Uint8ClampedArray. (.flat (to-array pixels))) width height))

(defn- draw! [ctx {::grid/keys [width height] :as grid}]
  (.putImageData ctx (image-data (->pixels grid) width height) 0 0))


(defn component []
  (let [grid @db/remote-grid-data]
    (if grid
      ^{:key "canvas"}
      [:canvas.shadow-xl
       {:ref #(when % ;; Sometimes called with nil
                (draw! (.getContext % "2d") grid))
        :width (::grid/width grid)
        :height (::grid/height grid)}]
      [:span "Loading..."])))
