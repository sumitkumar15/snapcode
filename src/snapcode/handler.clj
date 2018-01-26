(ns snapcode.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.cors :as cors]
            [ring.util.response :refer [response redirect]]
            [snapcode.evaluator :as ceval]
            [clojure.java.io :as io]
            [clojure.core.async :refer [go <!]]))

(defroutes app-routes
           (GET "/" [] (io/resource "public/index.html"))

           (POST "/upload"
                 request
             (println request)
             (let [res (ceval/eval-file (:body request))]
               (response res)))

           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (->
    app-routes
    wrap-json-body
    wrap-json-response
    (cors/wrap-cors :access-control-allow-origin #"http://localhost:3000/*"
                    :access-control-allow-methods [:get :post])))
