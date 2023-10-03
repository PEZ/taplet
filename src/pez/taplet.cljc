;; This is a `.cljc` file for the convenience of exercising the
;; Rich comment forms in either the ClojureScript or the Clojure REPL
;; (Calva makes this very convenient)

(ns pez.taplet
    #?(:cljs (:require-macros [pez.taplet :refer [let> let>l]])))

#?(:clj
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
       taps)))

#?(:clj
   (defmacro let>l
     "DEPRECATED Use metadata `^{:let> ...} bindings` instead.

     Like `let>`, adding a label first in the tapped vector."
     {:deprecated "1.0.0"}
     [label bindings & body]
     (assert (or (nil? label)
                 (keyword? label))
             "`label` is not a keyword")
     (let [taps (->taps label bindings #_{:clj-kondo/ignore [:unresolved-namespace]}
                                       (if (:ns &env)
                                         cljs.core/destructure
                                         destructure))]
       `(let [~@bindings]
          (tap> ~taps)
          ~@body))))

#?(:clj
   (defmacro let>
     "Like `let`, plus `let>`s the binding box (vector)

     Will add anything in the `:let>` key of the metadata of `bindings` as
     the first item in the tapped vector. Example:

     ``` clojure
     (let> ^{:let> :labeled-taps} [x 1
                                   y 2
                                   coords {:x x :y y}]
       coords) ;; => {:x 1, :y 2}
               ;; let> [:labeled-taps x 1 y 2 coords {:x 1, :y 2}]
     ```"
     [bindings & body]
     (let [label (:let> (meta bindings))
           taps (->taps label bindings #_{:clj-kondo/ignore [:unresolved-namespace]}
                                       (if (:ns &env)
                                         cljs.core/destructure
                                         destructure))]
       `(let [~@bindings]
          (tap> ~taps)
          ~@body))))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(comment
  (add-tap (partial println "tap>")) ;; Only for observability here

  (let> [x 1
         y 2
         coords {:x x :y y}]
    coords) ;; => {:x 1, :y 2}
           ;; tap> [x 1 y 2 coords {:x 1, :y 2}]

  (let> ^{:let> :labeled-taps} [x 1
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

