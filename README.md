# taplet

A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the binding box (that's what I call that vector).

Bonus: A macro named `let>l` that works like `let>`, and also will put a label in the beginning of the tapped vector.

**WIP, quite early so**, do not put in production. Though only meant for development so as long as you don't put in production you should be safe. 😄

## Usage

Dependency:

```clojure
[pez/taplet "0.1.1"]
```

Require:

```clojure
(require '[pez.taplet :refer [let> let>l]])
```

Now use `let>` or `let>l` wherever you have a `let` that you want to tap. 

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

