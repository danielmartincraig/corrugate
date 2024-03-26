(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy :refer [* /]]
            [emmy.matrix :as matrix]
            [emmy.util.permute :as permute]))

(defn euclidean-distance [[x1 y1] [x2 y2]]
  (emmy/sqrt (emmy/+ (emmy/square (emmy/- x2 x1))
                     (emmy/square (emmy/- y2 y1)))))

(def directions
  [[1 0] [0 1] [-1 0] [0 -1]])

(defn dfs [image components count root]
  (let [child-fn (fn [root]
                   (filter (and (fn [c] (every? #(<= 0 % 4) c))
                                (fn [c] (zero? (get-in @components c)))
                                (fn [c] (= (get-in image c) 1)))
                           (map (partial map + root) directions)))
        branch-fn (fn [c] (and (zero? (get-in @components c))
                               (= (get-in image c) 1)))
        walk (fn walk [c]
               (when (branch-fn c)
                 (swap! components assoc-in c count)
                 (mapcat walk (child-fn c))))]
    (walk root)))

(defn scan [image]
  (let [dimension (matrix/dimension image)
        components (atom (matrix/generate dimension dimension (constantly 0)))
        count (atom 0)]
    (doseq [i (range dimension)]
      (doseq [j (range dimension)]
        (when (and (= (get-in image [i j]) 1)
                   (zero? (get-in @components [i j])))
          (dfs image components (swap! count inc) [i j]))))
    @components))

(defn compute-determinant-of-first-component [image]
  (let [labeled-image (scan image)
        dimension (matrix/dimension image)
        indices (permute/cartesian-product [(range dimension) (range dimension)])
        first-component (filter #(= 1 (get-in labeled-image %)) indices)
        arcs (permute/cartesian-product [first-component first-component])
        distances (map (partial apply euclidean-distance) arcs)
        distance-matrix (apply matrix/by-rows (partition (count first-component) distances))]
    (emmy// (emmy/floor (emmy/* (matrix/determinant distance-matrix) 10000))
            10000)))

(defn compare-left-hand-image-and-right-hand-image [lh rh]
  (let [left-hand-signature (compute-determinant-of-first-component lh)
        right-hand-signature (compute-determinant-of-first-component rh)]
    (emmy/= left-hand-signature right-hand-signature)))

(comment

  (let [a (matrix/by-rows [0 0 0]
                          [0 0 0]
                          [0 1 1])
        ;;
        b (matrix/by-rows [0 1 0]
                          [0 1 0]
                          [0 0 0])]
    (compare-left-hand-image-and-right-hand-image a b))

  (let [a (matrix/by-rows [0 1 0]
                          [0 1 0]
                          [1 1 1])]
    (compute-determinant-of-first-component a))

  (let [a (matrix/by-rows [0 0 0]
                          [0 0 0]
                          [0 1 1])]
    (compute-determinant-of-first-component a))

  (let [a (matrix/by-rows [0 1 0]
                          [0 1 0]
                          [1 1 1])]
    (emmy// (emmy/floor (emmy/* (compute-determinant-of-first-component a) 1000))
            1000))

  (let [a (matrix/by-rows [1 0 0]
                          [1 1 1]
                          [1 0 0])]
    (matrix/determinant a)))

(rf/reg-sub :app/todos
            (fn [db _]
              (:todos db)))

(rf/reg-sub :app/images
            (fn [db _]
              (:images db)))

(rf/reg-sub :app/image
            :<- [:app/images]
            (fn [images [_ left-or-right]]
              (apply matrix/by-rows (left-or-right images))))

(rf/reg-sub :app/pixel-of-image
            :<- [:app/images]
            (fn [images [_ left-or-right row column]]
              (get-in images [left-or-right row column])))

(rf/reg-sub :app/comparison-result
            :<- [:app/image :left]
            :<- [:app/image :right]
            (fn [[left-hand-image right-hand-image] _]
              (if (compare-left-hand-image-and-right-hand-image left-hand-image right-hand-image) "Yes" "No")))

(rf/reg-sub :app/signature
            :<- [:app/image :left]
            (fn [image _]
              (str (compute-determinant-of-first-component image))))

(rf/reg-sub :app/legos
            (fn [db _]
              (:legos db)))

(rf/reg-sub :app/lego-suggestion
            :<- [:app/legos]
            :<- [:app/signature]
            (fn [[legos signature] _]
              (get legos signature)))

(rf/reg-sub :app/lego-suggestion-name
            :<- [:app/lego-suggestion]
            (fn [suggestion _]
              (:name suggestion)))