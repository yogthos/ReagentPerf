(ns football.core
  (:require
    [football.data :as data]
    [reagent.core :as reagent]
    [re-frame.core :refer [subscribe]]))

(def game-count 50)

(defn player-component [game-idx player-idx]
  (let [player @(subscribe [:player game-idx player-idx])]
    [:td
     [:div.player
      [:p.player__name
       [:span (:name player)]
       [:span.u-small (if (:invited-next-week? player) "Doing well" "Not coming again")]]
      [:div {:class-name (str "player__effort "
                              (if (< (:effort-level player) 5)
                                "player__effort--low"
                                "player__effort--high"))}]]]))

(defn game-component [idx]
  (let [game @(subscribe [:game idx])]
    [:tr
     [:td.u-center (:clock game)]
     [:td.u-center (-> game :score :home) "-" (-> game :score :away)]
     [:td.cell--teams (-> game :teams :home) "-" (-> game :teams :away)]
     [:td.u-center (:outrageous-ackles game)]
     [:td
      [:div.cards
       [:div.cards__card.cards__card--yellow (-> game :cards :yellow)]
       [:div.cards__card.cards__card--red (-> game :cards :red)]]]
     (for [player-idx (range (count (:players game)))]
       ^{:key player-idx}
       [player-component idx player-idx])]))

(defn games-component []
  [:tbody
   (for [i (range game-count)]
     ^{:key i}
     [game-component i])])

(defn games-table-component []
  [:table
   [:thead
    [:tr
     [:th {:width "50px"} "Clock"]
     [:th {:width "50px"} "Score"]
     [:th {:width "200px"} "Teams"]
     [:th "Outrageous Tackles"]
     [:th {:width "100px"} "Cards"]
     [:th {:width "100px"} "Players"]
     [:th {:width "100px"} ""]
     [:th {:width "100px"} ""]
     [:th {:width "100px"} ""]
     [:th {:width "100px"} ""]]]
   [games-component]])

(defn home-page []
  [games-table-component])

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (data/generate-games game-count)
  (data/update-games game-count)
  (mount-root))
