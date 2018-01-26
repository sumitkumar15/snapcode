(ns snapcode.evaluator
  (:require [clojure.core.async :refer [go <! >!! chan] :as async]
            [clojure.java.shell :as sh]
            [clojure.java.io :as io]))

(def timeout-time 4)

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defn generate-random-file
  [lang code]
  (let [r (rand-str 10)
        file (str "execfiles/" r)]
    (case lang
      "clojure" (do
                  (spit (str file ".clj") code)
                  (str file ".clj"))
      "python" (do
                 (spit (str file ".py") code)
                 (str file ".py"))
      "clisp" (do
                 (spit (str file ".lisp") code)
                 (str file ".lisp")))))


;(defn eval-code
;  [lang code-string]
;  (println code-string)
;  (let [d (get code-string "code")]
;    (cond
;      (= lang "clojure") (try
;                           {:status "success"
;                            :result (eval (read-string d))}
;                           (catch Exception e
;                             {:status "failed"
;                              :error (str e)}))
;      (= lang "python")
;      :default nil)))
(defn return-out
  [data]
  (println data)
  (if (= (:exit data) 0)
    {:status "success"
     :result (:out data)}
    {:status "failed"
     :error (:err data)}))

(defn eval-file
  [code-string]
  (println code-string)
  (let [lang (get code-string "lang")
        code (get code-string "code")
        file (generate-random-file lang code)]
    (cond
      (= lang "clojure") (let [res (sh/sh "clojure" file)]
                           (io/delete-file file)
                           (return-out res))

      (= lang "python") (let [res (sh/sh "python" file)]
                          (io/delete-file file)
                          (return-out res))
      (= lang "clisp") (let [res (sh/sh "clisp" file)]
                          (io/delete-file file)
                          (return-out res))
      :default nil)))
