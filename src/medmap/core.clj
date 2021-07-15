(ns medmap.core
  (:require [medmap.command :as command]
            [medmap.grid :as grid]))

(def WIDTH 700)
(def HEIGHT 700)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Assignment 1

(defn assignment-1
  "
  - Reads the input data
  - Manipulates a grid according to commands
  - Counts active cells in grid"
  []
  (let [grid (grid/make WIDTH HEIGHT)]
    (with-open [rdr (io/reader (io/resource "input-data.txt"))]
      (->> (command/parse-lines rdr)
           (command/apply-commands grid)
           ::grid/cells
           count))))

(comment
  ;; 7s (Probably because of spec)
  ;; Count => 336674
  (time
   (assignment-1))

  )
