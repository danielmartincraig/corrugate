(ns app.handlers
  (:require [re-frame.core :as rf]
            [app.fx :as fx]))

(def load-todos (rf/inject-cofx :store/todos "uix-starter/todos"))
(def store-todos (fx/store-todos "uix-starter/todos"))

(rf/reg-event-fx :app/init-db
                 [load-todos]
                 (fn [{:store/keys [todos]} [_ default-db]]
                   {:db (update default-db :todos into todos)}))

(rf/reg-event-fx :todo/add
                 [(rf/inject-cofx :time/now) store-todos]
                 (fn [{:keys [db]
                       :time/keys [now]}
                      [_ todo]]
                   {:db (assoc-in db [:todos now] todo)}))

(rf/reg-event-db :todo/remove
                 [store-todos]
                 (fn [db [_ created-at]]
                   (update db :todos dissoc created-at)))

(rf/reg-event-db :todo/set-text
                 [store-todos]
                 (fn [db [_ created-at text]]
                   (assoc-in db [:todos created-at :text] text)))

(rf/reg-event-db :todo/toggle-status
                 [store-todos]
                 (fn [db [_ created-at]]
                   (update-in db [:todos created-at :status] {:unresolved :resolved
                                                              :resolved :unresolved})))

(rf/reg-event-db :event/toggle-pixel
                 [store-todos]
                 (fn [db [_ left-or-right row column]]
                   (update-in db [:images left-or-right row column]
                              (fn [n] (if (zero? n) 1 0)))))