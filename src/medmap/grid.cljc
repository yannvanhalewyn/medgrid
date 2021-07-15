(ns medmap.grid
  "This namespace represents the network grid and how to manipulate it. The
  chosen datastructure for the network grid"
  (:require [clojure.spec.alpha :as s]))

(s/def ::width pos-int?)
(s/def ::height pos-int?)
(s/def ::coord (s/tuple pos-int? pos-int?))
(s/def ::cell boolean?)
(s/def ::cells (s/coll-of ::coord :kind set?))

(s/def ::grid (s/keys :req [::width ::height ::cells]))

(defn make [width height]
  {::width width
   ::height height
   ::cells #{}})

(defn toggle-set
  "Toggles the presence of an element in a set."
  [set elem]
  (if (contains? set elem)
    (disj set elem)
    (conj set elem)))

(defn toggle [grid coord]
  (update grid ::cells toggle-set coord))

(defn activate [grid coord]
  (update grid ::cells conj coord))

(defn deactivate [grid coord]
  (update grid ::cells disj coord))

(defn within?
  "Wether the coordinate is contained within the grid."
  [{::keys [width height]} [x y]]
  (and (pos-int? x) (pos-int? y)
       (< x width) (< y height)))

(defn subgrid->cells
  "Given a subgrid represented by the square contained between two points,
  returns a seq of all cell coordinates contained in that subgrid. The last
  point is inclusive, so we need to increment both values of the 'to'
  coordinate. Example: a subgrid from [0 0] to [2 2] would contain 9 cells,
  including [2 2]"
  [[from-x from-y] [to-x to-y]]
  (for [x (range from-x (inc to-x))
        y (range from-y (inc to-y))]
    [x y]))

(comment
  (-> (make 10 10) (toggle-cell [1 1]) )
  )
