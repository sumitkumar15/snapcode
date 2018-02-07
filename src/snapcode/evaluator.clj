(ns snapcode.evaluator
  (:require [clojure.core.async :refer [go <! >!! chan] :as async]
            [clojure.java.shell :as sh]
            [clojure.java.io :as io]))

(def state {})

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defn generate-random-file
  [lang code]
  (let [r (rand-str 10)
        file r]
    (case lang
      "clojure" (do
                  (spit (str "execfiles/" file ".clj") code)
                  (str file ".clj"))
      "python" (do
                 (spit (str "execfiles/" file ".py") code)
                 (str file ".py"))
      "clisp" (do
                 (spit (str "execfiles/" file ".lisp") code)
                 (str file ".lisp")))))

(defn return-out
  [data file]
  (println data)
  (cond
    (= 0 (:exit data))(do
                        (assoc state file (:out data))
                        {:status "success"
                         :result (:out data)
                         :fid file})
    (= 1 (:exit data)) (do
                         (assoc state file (:err data))
                         {:status "failed"
                          :error  (:err data)
                          :fid    file})
    (= 124 (:exit data)) (do
                           (assoc state file (:out data))
                           {:status "failed"
                            :error  "TLE"
                            :fid    file})))

(defn eval-file
  [session-key code-string]
  (println code-string)
  (let [lang (get code-string "lang")
        code (get code-string "code")
        temp (generate-random-file lang code)
        file (str "execfiles/" temp)]
    (cond
      (= lang "clojure") (let [res (sh/sh "timeout" "8" "clojure" file)]
                           ;(io/delete-file file)
                           (return-out res temp))

      (= lang "python") (let [res (sh/sh "timeout" "8" "python" file)]
                          ;(io/delete-file file)
                          (return-out res temp))

      (= lang "clisp") (let [res (sh/sh "timeout" "8" "clisp" file)]
                          ;(io/delete-file file)
                          (return-out res temp))

      :default {:result "failed"
                :error "Language not defined"})))

(defn get-file
  [filename]
  (slurp (str "execfiles/" filename)))

(defn get-exec-result
  [filename]
  (get state filename))

(defn get-file-res
  [filename]
  (try
    (let [data (get-file filename)]
      {:status "success"
       :name filename
       :content data
       :result (get-exec-result filename)})
    (catch Exception e
      (let [data (get-file filename)]
        {:status "success"
         :name filename
         :content data
         :result (get-exec-result filename)}))))
