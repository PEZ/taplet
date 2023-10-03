(ns pez.taplet-test
  (:require [clojure.core.async :as a]
   [clojure.test :refer [deftest testing is use-fixtures]]
            [pez.taplet :as sut]))

#?(:cljs (set! *exec-tap-fn* (fn [f] (f))))

(def ^:private tapped (a/chan (a/sliding-buffer 1)))
(def ^:private  save-tap (fn [v] (a/offer! tapped v)))
(defn- read-tapped [] (a/poll! tapped))

(defn- fixture [f]
  (add-tap save-tap)
  (f)
  (remove-tap save-tap))

(use-fixtures :once fixture)

(deftest let>
  (testing "Evaluates as `let`"
    (is (= [:foo :bar] (sut/let> [foo :foo
                                  bar :bar]
                                 [foo bar]))))

  (testing "Taps the binding box"
    (is (= [:foo :bar]
           (sut/let> [foo :foo
                      bar :bar]
                     [foo bar])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[foo :foo
             bar :bar]
           (read-tapped))))

  (testing "Handles vector destructuring"
    (is (= [:foo :bar :baz :gaz]
           (sut/let> [[a [b [c d]]] [:foo [:bar [:baz :gaz]]]]
                     [a b c d])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[a :foo
             b :bar
             c :baz
             d :gaz]
           (read-tapped))))

  (testing "Handles map destructuring"
    (is (= [2 {:x 2}]
           (sut/let> [{:keys [x] :as y} {:x 2}]
                     [x y])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[y
             {:x 2}
             x 2]
           (read-tapped))))

  (testing "Taps all the things as expected"
    (is (= [1 2 {:z 2} :foo :bar :baz :gaz]
           (sut/let> [x 1
                      {:keys [z] :as y} {:z 2}
                      [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
                     [x z y a b c d])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[x 1
             y
             {:z 2}
             z 2
             a :foo
             b :bar
             c :baz
             d :gaz]
           (read-tapped))))
  (testing "Labels the taps from metadata"
    (is (= [:foo :bar]
           (sut/let>  ^{:tap> :label}
                      [foo :foo
                       bar :bar]
                      [foo bar])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[:label
             foo :foo
             bar :bar]
           (read-tapped)))))

(deftest let>l
  (testing "Taps the binding box with the label first (even though deprecated)"
    (is (= [:foo :bar]
           #_{:clj-kondo/ignore [:unresolved-symbol :deprecated-var]}
           (sut/let>l :label
                      [foo :foo
                       bar :bar]
                      [foo bar])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= '[:label
             foo :foo
             bar :bar]
           (read-tapped)))))
