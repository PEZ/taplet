# taplet

A Clojure/ClojureScript macro, named `let>` that works like `let`, and also will `tap>` the binding vector.

## Usage

FIXME: write usage documentation!

Invoke a library API function from the command-line:

    $ clojure -X pez.taplet/foo :a 1 :b '"two"'
    {:a 1, :b "two"} "Hello, World!"

Run the project's tests (they'll fail until you edit them):

    $ clojure -M:test:runner

Build a deployable jar of this library:

    $ clojure -X:jar

This will update the generated `pom.xml` file to keep the dependencies synchronized with
your `deps.edn` file. You can update the version (and SCM tag) information in the `pom.xml` using the
`:version` argument:

    $ clojure -X:jar :version '"1.2.3"'

Install it locally (requires the `pom.xml` file):

    $ clojure -X:install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment
variables (requires the `pom.xml` file):

    $ clojure -X:deploy

Your library will be deployed to net.clojars.pez/taplet on clojars.org by default.

If you don't plan to install/deploy the library, you can remove the
`pom.xml` file but you will also need to remove `:sync-pom true` from the `deps.edn`
file (in the `:exec-args` for `depstar`).

## License

MIT License, see LICENSE file

