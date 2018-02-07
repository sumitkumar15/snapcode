# snapcode

Application to execute a code snippet from a provided image

It is the application backend created for 24 hr hackathon you can find the frontend [here](https://github.com/anirudhbs/code-exec)

The server is capable of executing clojure code.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

You need clojure installed 'clojure' should be present on your global environment variable. 

## Running

To start a web server for the application, run:

    lein ring server
