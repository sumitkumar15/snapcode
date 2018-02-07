(ns snapcode.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.cors :as cors]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [response redirect]]
            [snapcode.evaluator :as ceval]
            [clojure.java.io :as io]))

(defroutes app-routes
           (GET "/" [] (io/resource "public/index.html"))

           (GET "/files/:file" [file]
             (response (ceval/get-file-res file)))

           (POST "/upload"
                 request
             (println (:session request))
             (let [res (ceval/eval-file (:session request) (:body request))]
               (response res)))

           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (->
    app-routes
    wrap-session
    wrap-json-body
    wrap-json-response
    (cors/wrap-cors :access-control-allow-origin #"http://localhost:3000/*"
                    :access-control-allow-methods [:get :post])))
