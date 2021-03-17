(ns pez.taplet
  #?(:cljs
     (:require-macros [pez.taplet :refer [let> let>l]])))

(defmacro let>l
  "Like `let>`, adding a label first in the tapped vector"
  [label bindings & body]
  (assert (or (nil? label)
              (keyword? label))
          "`label` is not a keyword")
  (let [bindings (destructure bindings)
        symbolize (fn [sym] `(quote ~sym))
        gensymed? (fn [sym] (re-matches #"(map|vec)__\d+" (name sym)))
        taps (as-> bindings $
               (map first (partition 2 $))
               (map vector (map symbolize $) $)
               (remove (fn [[_ s]] (gensymed? s)) $)
               (into (if label [label] []) $))]
    `(let [~@bindings]
       (tap> ~taps)
       ~@body)))

(defmacro let>
  "Like `let`, plus `tap>`s the binding box"
  [bindings & body]
  (assert (vector? bindings)
          "`bindings` is not a vector")
  `(let>l nil ~bindings ~@body))
