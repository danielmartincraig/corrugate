(ns app.core
  (:require
   [cljs.spec.alpha :as s]
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [app.hooks :as hooks]
   [app.subs]
   [app.handlers]
   [app.fx]
   [app.db]
   [re-frame.core :as rf]))

(defui header []
  ($ :header.app-header
     ($ :h1 {:width 32}
        "Are these two shapes the same?")))

(defui footer []
  (let [result (hooks/use-subscribe [:app/comparison-result])]
    ($ :footer.app-footer
       ($ :div
          ($ :h2 result)
          ($ :small "made with "
             ($ :a {:href "https://github.com/pitch-io/uix"}
                "UIx"))))))

(defui pixel-view [{:keys [left-or-right row column]}]
  (let [p (hooks/use-subscribe [:app/pixel-of-image left-or-right row column])]
    ($ :button
       {:on-click #(rf/dispatch [:event/toggle-pixel left-or-right row column])}
       (if (zero? p) "O" "X"))))

(defui row-view [{:keys [row left-or-right]}]
  (let [image-dimension 3]
    ($ :div (for [column (range image-dimension)]
              ($ pixel-view {:key (str left-or-right "," row "," column)
                             :left-or-right left-or-right
                             :row row
                             :column column})))))

(defui image-view [{:keys [left-or-right]}]
  (let [image-dimension 3]
    ($ :div
       (for [row (range image-dimension)]
         ($ row-view {:key (str "row" row)
                      :row row
                      :left-or-right left-or-right})))))

(defui left-and-right-image-view []
  ($ :div
     ($ :div ($ image-view {:left-or-right :left}))

     ($ :div
        ($ image-view {:left-or-right :right}))))

(defui app []
  (let [todos (hooks/use-subscribe [:app/todos])]
    ($ :.app
       ($ header)
       ($ left-and-right-image-view)
       ($ footer))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.db/default-db])
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
