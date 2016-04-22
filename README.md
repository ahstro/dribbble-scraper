# dribbble-scraper

I wanted to learn some Clojure, so this is a proof-of-concept Dribbble scraper.

## Usage

This isn't meant to be used practically, so for now, the only way to use it
(that I know of) is to fire up a REPL and use the following functions:

```clojure
(scrape)            ; Starts at `dribbble.com/designers?page=1` and
                    ; scrapes each page until nothing is found

(scrape-page page)  ; Scrapes page `dribbble.com/designers?page={page}`
```

## License

Copyright © 2016 Anton Strömkvist

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
