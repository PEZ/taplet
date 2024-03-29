(ns etc.rcf-example)

(comment
  (require '[pez.taplet :refer [let>]])
  (add-tap (partial println "tap>")) ;; Only for observability here

  (let> [x 1
         y 2
         coords {:x x :y y}]
    coords) ;; => {:x 1, :y 2}
           ;; tap> [x 1 y 2 coords {:x 1, :y 2}]

  (let> ^{:let> :labeled-taps} [x 1
                                y 2
                                coords {:x x :y y}]
    coords) ;; => {:x 1, :y 2}
           ;; tap> [:labeled-taps x 1 y 2 coords {:x 1, :y 2}]

  (let> [x 1
         {:keys [z] :as y} {:z 2}
         [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
    [x z y a b c d]) ;; => [1 2 {:z 2} :foo :bar :baz :gaz]
                    ;; tap> [x 1 y {:z 2} z 2 a :foo b :bar c :baz d :gaz]

  #t (let [x 1
           y 2
           coords {:x x :y y}]
       (:x #t coords)) ;; => 1
                       ;; tap> {:x 1, :y 2}
                       ;; tap> 1
  :rcf)