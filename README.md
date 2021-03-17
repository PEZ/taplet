# taplet

A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the binding box (that's what I call that vector).

Bonus: A macro named `let>l` that works like `let>`, and also will put a label in the beginning of the tapped vector.

**Pre-alpha**, do not put in production. Though only meant for development so as long as you don't put in production you should be safe. 😄

## Usage

Dependency:

```clojure
[pez/taplet "0.1.0-SNAPSHOT"]
```

Require:

```clojure
(require '[pez.taplet :refer [let> let>l]])
```

Now use `let>` or `let>l` wherever you have a `let` that you want to tap. From the tests:


```clojure
(testing "Taps all the things as expected"
    (let [tapped (atom nil)
          save-tap (fn [v] (reset! tapped v))]
      (add-tap save-tap)
      (is (= [1 2 {:z 2} :foo :bar :baz :gaz]
           (sut/let> [x 1
                      {:keys [z] :as y} {:z 2}
                      [a [b {:keys [c d]}]] [:foo [:bar {:c :baz :d :gaz}]]]
                     [x z y a b c d])))
      (is (= [:x 1
              "{:keys [z], :as y}" {:keys [2] :as {:z 2}}
              :a :foo
              :b :bar
              "{:keys [c d]}" {:keys [:baz :gaz]}]
             @tapped))
      (remove-tap save-tap)))
```

## License

MIT License, see LICENSE file

