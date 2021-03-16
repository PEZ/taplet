(ns pez.taplet
  #?(:cljs
     (:require-macros [pez.taplet :refer [let>]])))

(defmacro let>
  "Like `let`, plus taps the binding box"
  [bindings & body]
  (let [to-vec (fn [bindings]
                 (let [b-names (flatten (partition 1 2 bindings))]
                   (reduce (fn [to-tap b-name]
                             (conj to-tap (keyword b-name) b-name))
                           []
                           b-names)))]
    `(let ~(destructure bindings)
       (tap> ~(to-vec bindings))
       ~@body)))
