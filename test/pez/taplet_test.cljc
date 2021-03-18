(ns pez.taplet-test
  (:require [clojure.core.async :as a]
   [clojure.test :refer [deftest testing is]]
            [pez.taplet :as sut]))

#?(:cljs (set! *exec-tap-fn* (fn [f] (f))))

(def tapped (a/chan (a/sliding-buffer 1)))
(def save-tap (fn [v] (a/offer! tapped v)))

(deftest let>
  (testing "Evaluates as `let`"
    (is (= [:foo :bar] (sut/let> [foo :foo
                                  bar :bar]
                                 [foo bar]))))

  (testing "Taps the binding box"
    (add-tap save-tap)
    (is (= [:foo :bar]
           (sut/let> [foo :foo
                      bar :bar]
                     [foo bar])))
    #?(:clj (a/<!! (a/timeout 10)))
    (is (= '[[foo :foo]
             [bar :bar]]
           (a/poll! tapped)))
    (remove-tap save-tap))

  (testing "Handles vector destructuring"
    (add-tap save-tap)
    (is (= [:foo :bar :baz :gaz]
           (sut/let> [[a [b [c d]]] [:foo [:bar [:baz :gaz]]]]
                     [a b c d])))
    #?(:clj (a/<!! (a/timeout 10)))
    (is (= '[[a :foo]
             [b :bar]
             [c :baz]
             [d :gaz]]
           (a/poll! tapped)))
    (remove-tap save-tap))

  (testing "Handles map destructuring"
    (add-tap save-tap)
    (is (= [2 {:x 2}]
           (sut/let> [{:keys [x] :as y} {:x 2}]
                     [x y])))
    #?(:clj (a/<!! (a/timeout 10)))
    (is (= '[[y
              {:x 2}]
             [x 2]]
           (a/poll! tapped)))
    (remove-tap save-tap))

  (testing "Taps all the things as expected"
    (add-tap save-tap)
    (is (= [1 2 {:z 2} :foo :bar :baz :gaz]
           (sut/let> [x 1
                      {:keys [z] :as y} {:z 2}
                      [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
                     [x z y a b c d])))
    #?(:clj (a/<!! (a/timeout 10)))
    (is (= '[[x 1]
             [y
              {:z 2}]
             [z 2]
             [a :foo]
             [b :bar]
             [c :baz]
             [d :gaz]]
           (a/poll! tapped)))
    (remove-tap save-tap)))

(deftest let>l
  (testing "Taps the binding box with the label first"
    (add-tap save-tap)
    (is (= [:foo :bar]
           (sut/let>l :label
                      [foo :foo
                       bar :bar]
                      [foo bar])))
    #?(:clj (a/<!! (a/timeout 10)))
    (is (= '[:label
             [foo :foo]
             [bar :bar]]
           (a/poll! tapped)))
    (remove-tap save-tap)))
