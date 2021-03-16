(ns pez.taplet
  #?(:cljs
     (:require-macros [pez.taplet :refer [let>]])))

(defmacro let>
  "Like `let`, plus `tap>`s the binding box"
  [bindings & body]
  (let [to-vec (fn [bindings]
                 (into []
                       (mapcat (fn [b-name]
                                 [(if (symbol? b-name) (keyword b-name) :-destruct-) b-name])
                               (flatten (partition 1 2 bindings)))))]
    `(let ~(destructure bindings)
       (tap> ~(to-vec bindings))
       ~@body)))
