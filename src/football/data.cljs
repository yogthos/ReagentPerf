(ns football.data
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub]]))

(defn generate-fake-player []
  {:name               (-> js/faker .-name (.findName))
   :effort-level       (rand-int 10)
   :invited-next-week? (> (rand) 0.5)})

(defn generate-fake-game []
  {:id                (-> js/faker .-random (.uuid))
   :clock             0
   :score             {:home 0 :away 0}
   :teams             {:home (-> js/faker .-address (.city))
                       :away (-> js/faker .-address (.city))}
   :outrageous-ackles 0
   :cards             {:yellow 0 :red 0}
   :players           (mapv generate-fake-player (range 4))})

(reg-event-db
  :initialize
  (fn [_ [_ game-count]]
    (mapv generate-fake-game (range game-count))))

(reg-sub
  :player
  (fn [games [_ game-idx player-idx]]
    (get-in games [game-idx :players player-idx])))

(reg-sub
  :game
  (fn [games [_ idx]] (games idx)))

(defn generate-games [game-count]
  (dispatch [:initialize game-count]))

(defn maybe-update [game prob path f]
  (if (< (rand-int 100) prob)
    (update-in game path f)
    game))

(defn update-rand-player [game idx]
  (-> game
      (assoc-in [:players idx :effort-level] (rand-int 10))
      (assoc-in [:players idx :invited-next-week?] (> (rand) 0.5))))

(reg-event-db
  :update-game
  (fn [games [_ idx]]
    (update
      games idx
      #(-> %
           (update :clock inc)
           (maybe-update 5 [:score :home] inc)
           (maybe-update 5 [:score :away] inc)
           (maybe-update 8 [:cards :yellow] inc)
           (maybe-update 2 [:cards :red] inc)
           (maybe-update 10 [:outrageous-ackles] inc)
           (update-rand-player (rand-int 4))))))


(defn update-game-at-interval [interval idx]
  (dispatch [:update-game idx])
  (js/setTimeout update-game-at-interval interval interval idx))

(def event-interval 1000)

(defn update-games [game-count]
  (dotimes [i game-count]
    (dispatch [:update-game i])
    (js/setTimeout #(update-game-at-interval event-interval i)
                   (* i event-interval))))

