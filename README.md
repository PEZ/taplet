# taplet

A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the let box (that's what I call that bininding vector).

## Usage

Dependency


``` clojure
;; deps.edn
{pez/taplet {:mvn/version "1.0.0"}}

;; Leiningen
[pez/taplet "1.0.0"]
```

Use `let>` wherever you have a `let` that you want to tap. Optionally label the taps using `{:let> :some-label}` as metadata for the binding vector.

```clojure
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
```

## License

MIT License, see LICENSE file

