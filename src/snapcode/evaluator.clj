(ns snapcode.evaluator)

(defn eval-code
  [lang code-string]
  (let [d (get code-string "code")]
    (cond
      (= lang "clojure") (try
                           {:status "success"
                            :result (eval (read-string d))}
                           (catch Exception e
                             {:status "failed"
                              :error (str e)}))
      :default nil)))
