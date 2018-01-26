(ns snapcode.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.cors :as cors]
            [ring.util.response :as response]
            [snapcode.evaluator :as ceval]))

(defroutes app-routes
           (GET "/" [] (response/file-response "../../resources/public/index.html"))
           (POST "/upload"
                 request
             (ceval/eval-code "clojure" (:body request)))
           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (->
    app-routes
    wrap-json-body
    wrap-json-response
    (cors/wrap-cors :access-control-allow-origin #"http://localhost:3449/*"
                    :access-control-allow-methods [:get :post])))
