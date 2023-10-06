;; This is a `.cljc` file for the convenience of exercising the
;; Rich comment forms in either the ClojureScript or the Clojure REPL
;; (Calva makes this very convenient)

(ns pez.hasht
  (:require [clojure.walk :as walk]))

(def t-prefix "#t")

(def result-sym (gensym "result"))

;; Stolen from hashp
(defn- hide-t-form [form]
  (if (and (seq? form)
           (vector? (second form))
           (= (-> form second first) result-sym))
    (-> form second second)
    form))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn t* [form]
  (let [orig-form (walk/postwalk hide-t-form form)]
    `(let [~result-sym ~form]
       (do
         (tap> ~result-sym)
         ~result-sym))))

(comment
  (add-tap (partial println "tap>")) ;; Only for observability here

  #t (let [x 1
           y 2
           coords {:x x :y y}]
       (:x #t coords)) ;; => 1
                       ;; tap> {:x 1, :y 2}
                       ;; tap> 1
  :rcf)

