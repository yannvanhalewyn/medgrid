(ns medmap.command
  "This namespace is all about the commands that manipulate the network grid. It
  is responsible for parsing the commands provided by the mobile
  networking company and executing them over a grid."
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [medmap.grid :as grid]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs

(s/def ::raw-operation #{"toggle" "activate" "deactivate"})
(s/def ::raw-coordinate1 (s/and string? #(re-matches #"\d+" %)))
(s/def ::raw-coordinate (s/cat :coord/x ::raw-coordinate1 :coord/y ::raw-coordinate1))

(s/def ::raw-command (s/cat :raw/operation ::raw-operation
                            :raw/from ::raw-coordinate
                            :_skip any?
                            :raw/to ::raw-coordinate))

(s/def ::coordinate (s/tuple pos-int? pos-int?))
(s/def ::from ::coordinate)
(s/def ::to ::coordinate)
(s/def ::operation #{::toggle ::activate})

(s/def ::command (s/cat :operation ::operation
                        :parameters (s/keys :req [::from ::to])))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers

(defn- conform!
  "Conforms the spec and throws an exception if invalid."
  [spec data]
  (let [result (s/conform spec data)]
    (if (= ::s/invalid result)
      (throw (ex-info "Raw input command invalid!"
                      {::command data
                       ::errors (s/explain-data spec data)}))
      result)))

(defn- parse-int
  "Assumes a valid string number as input."
  [s]
  (Integer. s))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing

(defn- parse-command
  "Parses a raw text command. Format is determined by the mobile network
  providers. Commands are of the form '<operation> <from> to <to>'.

  Example inputs:
    toggle 508,84 to 545,462
    activate 192,165 to 528,652
    deactivate 22,362 to 30,378

  Outputs a command vector. Example:
    [::command/toggle {::command/from [x, y] ::command/to [x y]}]"
  [raw-command]
  (let [{:raw/keys [operation from to]}
        (conform! ::raw-command (str/split raw-command #"[,\s]"))
        parse-coord (fn [{:coord/keys [x y]}] [(parse-int x) (parse-int y)])]
    [(keyword "medmap.command" operation)
     {::from (parse-coord from)
      ::to (parse-coord to)}]))

(defn parse-lines
  "Parses the lines from an input reader where each line is a raw command.
  Returns a list of command vectors. Pass in a reader and handle file closing
  side effects elsewhere. Returns a lazy sequence of command vectors, reading
  the lines in the file as the parser progresses."
  [rdr]
  (map parse-command (line-seq rdr)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Manipulating the grid

(defn- apply-subgrid
  "Applies a transformation function f to every cell of the subgrid of the
  command parameters."
  [f grid {::keys [from to]}]
  (reduce f grid (grid/subgrid->cells from to)))

(defmulti apply-command (fn [_grid cmd] (first cmd)))

(defmethod apply-command :default [grid cmd]
  (throw (ex-info "Cannot process command" {::command cmd})))

(defmethod apply-command ::toggle [grid [_ params]]
  (apply-subgrid grid/toggle grid params))

(defmethod apply-command ::activate [grid [_ params]]
  (apply-subgrid grid/activate grid params))

(defmethod apply-command ::deactivate [grid [_ params]]
  (apply-subgrid grid/deactivate grid params))

(defn apply-commands
  "Given a grid, iterate over all the commands and execute them. Returns a new
  grid."
  [grid commands]
  (reduce apply-command grid commands))

(comment

  (parse-command "toggle 192,165 to 528,652")

  (with-open [rdr (io/reader (io/resource "input-data.txt"))]
    (doall (parse-file rdr)))

  (-> (grid/make 10 10)
      (apply-command [::toggle {::from [0 0] ::to [2 2]}])
      (apply-command [::deactivate {::from [0 0] ::to [1 1]}])
      (apply-command [::activate {::from [2 2] ::to [3 3]}])
      ::grid/cells
      count)

  )
