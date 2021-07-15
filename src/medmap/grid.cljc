(ns medmap.grid
  "This namespace represents the network grid and how to manipulate it. The
  chosen datastructure for the network grid is map containing width, height and
  a set of the active cells. This makes it easy to toggle or manipulate cells,
  and also might decrease the data and network footprint when the network
  providers decide to deactivate half the country because of a bandwith
  shortage, which is desirable. Because there is a bandwith shortage."
  (:require [clojure.spec.alpha :as s]))


(s/def ::width pos-int?)
(s/def ::height pos-int?)
(s/def ::coord (s/tuple pos-int? pos-int?))
(s/def ::cells (s/coll-of ::coord :kind set?))

(s/def ::grid (s/keys :req [::width ::height ::cells]))

(defn- toggle-set
  "Utility function that toggles the presence of an element in a set."
  [set elem]
  (if (contains? set elem)
    (disj set elem)
    (conj set elem)))

(defn make
  "Makes a new empty grid of width and height. Defaults to all cells being
  inactive."
  [width height]
  {::width width
   ::height height
   ::cells #{}})

(defn toggle
  "Toggles network activity at given coordinates."
  [grid coord]
  (update grid ::cells toggle-set coord))

(defn activate
  "Activates network activity at given coordinates."
  [grid coord]
  (update grid ::cells conj coord))

(defn deactivate
  "Disables network activity at given coordinates."
  [grid coord]
  (update grid ::cells disj coord))

(defn active?
  "Wether the cell at that position is active or not"
  [{::keys [cells]} coord]
  (contains? cells coord))

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
  (-> (make 10 10)
      (toggle [1 1])
      (activate [2 1])
      (toggle [2 1]))
  )
