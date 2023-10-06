(ns pez.hasht-test
  (:require [clojure.core.async :as a]
            [clojure.test :refer [deftest testing is use-fixtures]]))

#?(:cljs (set! *exec-tap-fn* (fn [f] (f))))

(def ^:private tapped (a/chan (a/sliding-buffer 1)))
(def ^:private  save-tap (fn [v] (a/offer! tapped v)))
(defn- read-tapped [] (a/poll! tapped))

(defn- fixture [f]
  (add-tap save-tap)
  (f)
  (remove-tap save-tap))

(use-fixtures :once fixture)

(deftest hasht
  (testing "Evaluates as if #t is not there"
    (is (= [:foo ["bar" :bar]] (let [foo :foo
                                     bar ["bar" :bar]]
                                 [foo #t bar]))))

  (testing "Taps the #t tagged form"
    (is (= [:foo :bar]
           (let [foo :foo
                 bar :bar]
             #t [foo bar])))
    #?(:clj (a/<!! (a/timeout 1)))
    (is (= [:foo :bar]
           (read-tapped)))))
