(ns app.db)

(def default-db
  {:todos (sorted-map-by >)
   :legos {"1: (-1*{} + 1*{0 2})" {:name      "43857"
                                   :signature "1: (-1*{} + 1*{0 2})"}}
   :images {:left [[1 0 0]
                   [0 0 0]
                   [0 0 0]]
            :right [[0 1 0]
                    [0 1 0]
                    [0 1 0]]}})
