# taplet

Utilities for tapping Clojure forms.

1. A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the let box (that's what I call that bininding vector).
2. A reader tag, `#t` that will tap the tagged form.

## Usage

Dependency


``` clojure
;; deps.edn
{pez/taplet {:mvn/version "1.0.58"}}

;; Leiningen
[pez/taplet "1.0.58"]
```

## let>

Use `let>` wherever you have a `let` that you want to tap. Optionally label the taps using `{:let> :some-label}` as metadata for the binding vector.

```clojure
(comment
  (require '[pez.taplet :refer [let>]])

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
```

## #t

``` clojure
(comment
  (add-tap (partial println "tap>")) ;; Only for observability here

  #t (let [x 1
           y 2
           coords {:x x :y y}]
       (:x #t coords)) ;; => 1
                       ;; tap> {:x 1, :y 2}
                       ;; tap> 1
  :rcf)
```

## License

MIT License, see LICENSE file

