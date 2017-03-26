(ns football.data
  (:require [reagent.core :as reagent]))

(defonce games (reagent/atom nil))

(defn generate-fake-player []
  {:name               (-> js/faker .-name (.findName))
   :effort-level       (rand-int 10)
   :invited-next-week? (> (rand) 0.5)})

(defn generate-fake-game []
  {:id                 (-> js/faker .-random (.uuid))
   :clock              0
   :score              {:home 0 :away 0}
   :teams              {:home (-> js/faker .-address (.city))
                        :away (-> js/faker .-address (.city))}
   :outrageous-tackles 0
   :cards              {:yellow 0 :red 0}
   :players            (mapv generate-fake-player (range 4))})

(defn generate-games [game-count]
  (reset! games (mapv generate-fake-game (range game-count))))

(defn maybe-update [game prob path f]
  (if (< (rand-int 100) prob)
    (update-in game path f)
    game))

(defn update-rand-player [game idx]
  (-> game
      (assoc-in [:players idx :effort-level] (rand-int 10))
      (assoc-in [:players idx :invited-next-week?] (> (rand) 0.5))))

(defn update-game [game]
  (-> game
      (update :clock inc)
      (maybe-update 5 [:score :home] inc)
      (maybe-update 5 [:score :away] inc)
      (maybe-update 8 [:cards :yellow] inc)
      (maybe-update 2 [:cards :red] inc)
      (maybe-update 10 [:outrageous-tackles] inc)
      (update-rand-player (rand-int 4))))

(defn update-game-at-interval [interval idx]
  (swap! games update idx update-game)
  (js/setTimeout update-game-at-interval interval interval idx))

(def event-interval 1000)

(defn update-games [game-count]
  (dotimes [i game-count]
    (swap! games update i update-game)
    (js/setTimeout #(update-game-at-interval event-interval i)
                   (* i event-interval))))
