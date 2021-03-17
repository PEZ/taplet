(ns pez.taplet
  #?(:cljs
     (:require-macros [pez.taplet :refer [let> let>l]])))

(defmacro let>l
  "Like `let>`, adding a label first in the tapped vector"
  [label bindings & body]
  (assert (or (nil? label)
              (keyword? label))
          "`label` is not a keyword")
  (let [to-vec (fn [bindings]
                 (into (if label [label] [])
                       (mapcat (fn [bind-to]
                               [(if (symbol? bind-to)
                                  (keyword bind-to)
                                  (pr-str bind-to))
                                bind-to])
                             (flatten (partition 1 2 bindings)))))]
    `(let ~(destructure bindings)
       (tap> ~(to-vec bindings))
       ~@body)))

(defmacro let>
  "Like `let`, plus `tap>`s the binding box"
  [bindings & body]
  (assert (vector? bindings)
          "`bindings` is not a vector")
  `(let>l nil ~bindings ~@body))
