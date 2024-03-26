(ns app.db)

(def default-db
  {:todos (sorted-map-by >)
   :legos {"0" {:name "18654"
                :signature "0"}
           "4" {:name "32523"
                :signature "4"}
           "47781/2500" {:name      "60484"
                         :signature "47781/2500"}
           "-1" {:name "43857"
                 :signature "-1"}}
   :images {:left [[1 0 0]
                   [0 0 0]
                   [0 0 0]]
            :right [[0 1 0]
                    [0 1 0]
                    [0 1 0]]}})
