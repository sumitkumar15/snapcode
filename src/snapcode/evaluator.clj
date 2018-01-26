(ns snapcode.evaluator)

(defn eval-code
  [lang code-string]
  (let [d (:code code-string)]
    (cond
      (= lang "clojure") (eval (read-string d)))))
