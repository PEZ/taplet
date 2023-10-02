# taplet

A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the binding box (that's what I call that vector).


## Usage

Dependency:

```clojure
{:deps {pez/taplet {:mvn/version "0.1.3}}}
```

```clojure
[pez/taplet "0.1.3"]
```

Require:

```clojure
(require '[pez.taplet :refer [let> let>l]])
```

Now use `let>` wherever you have a `let` that you want to tap. 

```clojure
(let> [x 1
       {:keys [z] :as y} {:z 2}
       [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
  [x z y a b c d])

=> [1 2 {:z 2} :foo :bar :baz :gaz]

tap> [[x 1]
      [y
       {:z 2}]
      [z 2]
      [a :foo]
      [b :bar]
      [c :baz]
      [d :gaz]]
```

## License

MIT License, see LICENSE file

