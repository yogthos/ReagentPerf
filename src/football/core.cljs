(ns football.core
  (:require
    [football.data :as data]
    [reagent.core :as reagent]))

(defn player-component [{:keys [name invited-next-week? effort-level]}]
  [:td
   [:div.player
    [:p.player__name
     [:span name]
     [:span.u-small (if invited-next-week? "Doing well" "Not coming again")]]
    [:div {:class-name (str "player__effort "
                            (if (< effort-level 5)
                              "player__effort--low"
                              "player__effort--high"))}]]])

(defn game-component [game]
  [:tr
   [:td.u-center (:clock game)]
   [:td.u-center (-> game :score :home) "-" (-> game :score :away)]
   [:td.cell--teams (-> game :teams :home) "-" (-> game :teams :away)]
   [:td.u-center (:outrageous-tackles game)]
   [:td
    [:div.cards
     [:div.cards__card.cards__card--yellow (-> game :cards :yellow)]
     [:div.cards__card.cards__card--red (-> game :cards :red)]]]
   (for [player (:players game)]
     ^{:key player}
     [player-component player])])

(defn games-component []
  [:tbody
   (for [game @data/games]
     ^{:key game}
     [game-component game])])

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

(def game-count 50)

(defn init! []
  (data/generate-games game-count)
  (data/update-games game-count)
  (mount-root))
