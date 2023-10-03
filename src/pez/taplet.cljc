(ns pez.taplet
  (:require cljs.core)
  #?(:cljs (:require-macros [pez.taplet :refer [let> let>l]])))

(defn- ->taps
  [label bindings destructure]
  (assert (vector? bindings)
          "`bindings` is not a vector")
  (let [bindings (destructure bindings)
        symbolize (fn [sym] `(quote ~sym))
        gensymed? (fn [sym] (re-matches #"(map|vec)__\d+" (name sym)))
        taps (as-> bindings $
                   (map first (partition 2 $))
                   (map vector (map symbolize $) $)
                   (remove (fn [[_ s]] (gensymed? s)) $)
                   (apply concat $)
                   (into (if label [label] []) $))]
    taps))

(defmacro let>l
  "DEPRECATED Use metadata `^{:tap> ...} bindings` instead.

   Like `let>`, adding a label first in the tapped vector."
  {:deprecated "1.0.0"}
  [label bindings & body]
  (assert (or (nil? label)
              (keyword? label))
          "`label` is not a keyword")
  (let [taps (->taps label bindings #?(:cljs cljs.core/destructure
                                       :clj destructure))]
    `(let [~@bindings]
       (tap> ~taps)
       ~@body)))

(defmacro let>
  "Like `let`, plus `tap>`s the binding box (vector)

   Will add anything in the `:tap>` key of the metadata of `bindings` as
   the first item in the tapped vector."
  [bindings & body]
  (let [label (:tap> (meta bindings))
        taps (->taps label bindings #?(:cljs cljs.core/destructure
                                       :clj destructure))]
    `(let [~@bindings]
       (tap> ~taps)
       ~@body)))

(comment
 (add-tap (partial println "tap>")) ;; Only for observability here

 (let> [x 1
        y 2
        coords {:x x :y y}]
   coords) ;; => {:x 1, :y 2}
           ;; tap> [x 1 y 2 coords {:x 1, :y 2}]

 (let> ^{:tap> :labeled-taps} [x 1
                               y 2
                               coords {:x x :y y}]
   coords) ;; => {:x 1, :y 2}
           ;; tap> [:labeled-taps x 1 y 2 coords {:x 1, :y 2}]

 (let> [x 1
        {:keys [z] :as y} {:z 2}
        [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
   [x z y a b c d]) ;; => [1 2 {:z 2} :foo :bar :baz :gaz]
                    ;; tap> [x 1 y {:z 2} z 2 a :foo b :bar c :baz d :gaz]

 :rcf)

