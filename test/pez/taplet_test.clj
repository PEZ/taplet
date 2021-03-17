(ns pez.taplet-test
  (:require [clojure.test :refer [deftest testing is]]
            [pez.taplet :as sut]))


(deftest let>
  (testing "Evaluates as `let`"
    (is (= [:foo :bar] (sut/let> [foo :foo
                                  bar :bar]
                                 [foo bar]))))

  (testing "Taps the binding box"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [:foo :bar]
             (sut/let> [foo :foo
                        bar :bar]
                       [foo bar])))
      (is (= [:foo :foo
              :bar :bar]
             @tapped))
      (remove-tap save-tap)))

  (testing "Taps vector destructuring bind-to flattened"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [:foo :bar :baz :gaz]
             (sut/let> [[a [b [c d]]] [:foo [:bar [:baz :gaz]]]]
                       [a b c d])))
      (is (= [:a :foo
              :b :bar
              :c :baz
              :d :gaz]
             @tapped))
      (remove-tap save-tap)))

  (testing "Taps map destructuring bind-to as a string"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [2 {:x 2}]
             (sut/let> [{:keys [x] :as y} {:x 2}]
                       [x y])))
      (is (= ["{:keys [x], :as y}" {:keys [2] :as {:x 2}}]
             @tapped))
      (remove-tap save-tap)))

  (testing "Taps all the things as expected"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [1 2 {:z 2} :foo :bar :baz :gaz]
           (sut/let> [x 1
                      {:keys [z] :as y} {:z 2}
                      [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
                     [x z y a b c d])))
      (is (= [:x 1
              "{:keys [z], :as y}" {:keys [2] :as {:z 2}}
              :a :foo
              :b :bar
              "{:keys [c d]}" {:keys [:baz :gaz]}]
             @tapped))
      (remove-tap save-tap))))

(deftest let>l
  (testing "Taps the binding box with the label first"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [:foo :bar]
             (sut/let>l :label
                        [foo :foo
                         bar :bar]
                        [foo bar])))
      (is (= [:label
              :foo :foo
              :bar :bar]
             @tapped))
      (remove-tap save-tap))))
