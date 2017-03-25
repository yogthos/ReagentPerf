(ns football.data
  (:require [reagent.core :as reagent]))

(defonce games (reagent/atom []))

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
   :cards             {:yellow 0 :read 0}
   :players           (mapv generate-fake-player (range 4))})

(defn generate-games [game-count]
  (reset! games (map generate-fake-game (range game-count))))

(defn maybe-update [game prob path f]
  (if (< (rand-int 100) prob)
    (update-in game path f)
    game))

(defn update-rand-player [game idx]
  (-> game
      (assoc-in [:players idx :effort-level] (rand-int 10))
      (assoc-in [:players idx :invited-next-week?] (> (rand) 0.5))))

(defn update-games []
  (swap! games
         #(for [game %]
            (-> game
                (update :clock inc)
                (maybe-update 5 [:score :home] inc)
                (maybe-update 5 [:score :away] inc)
                (maybe-update 8 [:cards :yellow] inc)
                (maybe-update 2 [:cards :red] inc)
                (maybe-update 10 [:outrageous-ackles] inc)
                (update-rand-player (rand-int 4)))))
  (js/setTimeout update-games 1000))

