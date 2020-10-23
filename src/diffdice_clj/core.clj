(ns diffdice-clj.core)

(defn dice
  [sides]
  #(+ 1 (rand-int sides)))

(defn save
  [modifier condition dice]
  (fn []
    (let [current (dice)]
      (if (condition current)
        (modifier current)
        current))))

(defn reroll
  [dice]
  (fn
    [current]
    (dice)))

(defn addone
  [current]
  (+ 1 current))

(defn success-rate
  [dice n]
  (float
    (/
     (count
       (filter
         #(> % 4)
         (take n (repeatedly dice))))
     n)))


(defn main
  []
  (println 
    (let
      [dice (dice 6)
       failure #(< % 5)
       save-vs-failure #(save %1 failure %2)]
      { :raw (success-rate
               dice
               1e6)
       :reroll (success-rate
                 (save-vs-failure ( reroll dice ) dice)
                 1e6)
       :reroll-twice (success-rate
                       (->> dice
                            ( save-vs-failure ( reroll dice))
                            ( save-vs-failure ( reroll dice)))
                       1e6)
       :addone (success-rate
                 (save-vs-failure addone dice)
                 1e6)
       :addone-twice (success-rate
                       (->>
                         dice
                         (save-vs-failure addone)
                         (save-vs-failure addone))
                       1e6)})))
