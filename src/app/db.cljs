(ns app.db)

(def default-db
  {:todos (sorted-map-by >)
   :images {:left [[1 0 0]
                   [0 0 0]
                   [0 0 0]]
            :right [[0 1 0]
                    [0 1 0]
                    [0 1 0]]}})
