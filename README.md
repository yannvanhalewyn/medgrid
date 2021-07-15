## Running it locally

1. **Start the server**

Start a REPL with your prefered approach (this project uses tools.deps). From the REPL you can start the server:

``` clojure
=> (in-ns 'medmap.server)
;; => #namespace[medmap.server]
=> (start!)
```

2. **Start CLJS compilation**

``` sh
$ yarn start
```

3. **Open browser**

And point it to `http://localhost:8080`
