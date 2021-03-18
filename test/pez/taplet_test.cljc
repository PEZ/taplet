(ns pez.taplet-test
  (:require [clojure.test :refer [deftest testing is]]
            [pez.taplet :as sut]))

(defn sync-fn [f] (f))
(def ^:dynamic *dummy* nil)

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
             (binding [#?(:clj *dummy*
                          :cljs *exec-tap-fn*) sync-fn]
               (sut/let> [foo :foo
                          bar :bar]
                         [foo bar]))))
      (is (= '[[foo :foo]
               [bar :bar]]
             @tapped))
      (remove-tap save-tap)))

  (testing "Handles vector destructuring"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [:foo :bar :baz :gaz]
             (binding [#?(:clj *dummy*
                          :cljs *exec-tap-fn*) sync-fn]
               (sut/let> [[a [b [c d]]] [:foo [:bar [:baz :gaz]]]]
                         [a b c d]))))
      (is (= '[[a :foo]
               [b :bar]
               [c :baz]
               [d :gaz]]
             @tapped))
      (remove-tap save-tap)))

  (testing "Handles map destructuring"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [2 {:x 2}]
             (binding [#?(:clj *dummy*
                          :cljs *exec-tap-fn*) sync-fn]
               (sut/let> [{:keys [x] :as y} {:x 2}]
                         [x y]))))
      (is (= '[[y
                {:x 2}]
               [x 2]]
             @tapped))
      (remove-tap save-tap)))

  (testing "Taps all the things as expected"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [1 2 {:z 2} :foo :bar :baz :gaz]
             (binding [#?(:clj *dummy*
                          :cljs *exec-tap-fn*) sync-fn]
               (sut/let> [x 1
                          {:keys [z] :as y} {:z 2}
                          [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
                         [x z y a b c d]))))
      (is (= '[[x 1]
               [y
                {:z 2}]
               [z 2]
               [a :foo]
               [b :bar]
               [c :baz]
               [d :gaz]]
             @tapped))
      (remove-tap save-tap))))

(deftest let>l
  (testing "Taps the binding box with the label first"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [:foo :bar]
             (binding [#?(:clj *dummy*
                          :cljs *exec-tap-fn*) sync-fn]
               (sut/let>l :label
                          [foo :foo
                           bar :bar]
                          [foo bar]))))
      (is (= '[:label
               [foo :foo]
               [bar :bar]]
             @tapped))
      (remove-tap save-tap))))
