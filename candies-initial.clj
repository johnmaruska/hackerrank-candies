(def fptr (get (System/getenv) "OUTPUT_PATH"))

(def n (Integer/parseInt (clojure.string/trim (read-line))))

(def arr [])

(doseq [_ (range n)]
  (def arr (conj arr (Integer/parseInt (read-line)))))

;; Tracking array/vector of calculated candy values
(def candy-arr (atom (vec (take n (repeat -1)))))

(defn local-min?
  "true if target is not higher value than both left and right"
  [target left right]
  (and (<= target left)
       (<= target right)))

(defn local-max?
  "true if target is higher value than both left and right"
  [target left right]
  (and (> target left)
       (> target right)))

(defn safe-get-entry
  "Gets an entry at index idx from indexed coll of size m"
  [m coll idx]
  (if (> m idx -1)
    (nth coll idx)
    0))

(defn get-candy-value
  "Calculate the candy value for an index and return it.

  As a prerequisite of the calculation, other candy values are calculated
  and stored in @candy-arr to be returned when requested."
  [m rank-arr idx]
  (let [stored-candy-val (safe-get-entry m @candy-arr idx)
        get-entry (partial safe-get-entry m rank-arr)
        get-value (partial get-candy-value m rank-arr)]
    (cond
      ;; candy value already stored
      (not (= stored-candy-val -1)) stored-candy-val
      ;; index out of bounds, assume a value of zero
      (or (< idx 0) (>= idx m)) 0
      ;; not stored and in bounds, calculate value
      :else
      (let [itm (get-entry idx)
            left-idx (- idx 1)
            right-idx (+ idx 1)
            left (get-entry left-idx)
            right (get-entry right-idx)
            candy-val (cond
                        ;; local minimum includes equal elements, e.g. [1 1 1]
                        (local-min? itm left right)
                        1
                        ;; more than both neighbors
                        (local-max? itm left right)
                        (+ 1 (max (get-value right-idx)
                                  (get-value left-idx)))
                        ;; higher than left, not higher than left
                        (> itm left)
                        (+ 1 (get-value left-idx))
                        ;; higher than right, not higher than left
                        (> itm right)
                        (+ 1 (get-value right-idx)))]
        ;; store calculated value
        (swap! candy-arr assoc idx candy-val)
        ;; return so we can make a sequence with map if we want
        candy-val))))

(defn candies
  "Calculate the total number of candies required for coll"
  [num coll]
  ;; calculate all values
  (doall (map-indexed (fn [idx itm] (get-candy-value num (vec coll) idx)) coll))
  ;; sum them
  (apply + @candy-arr))

(def result (candies n arr))
(spit fptr (str result "\n") :append true)
