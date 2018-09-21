(def fptr (get (System/getenv) "OUTPUT_PATH"))

(def n (Integer/parseInt (clojure.string/trim (read-line))))

(def arr [])

(doseq [_ (range n)]
  (def arr (conj arr (Integer/parseInt (read-line)))))

(def candy-arr (atom (vec (take n (repeat -1)))))

(defn local-min? [target left right]
  (and (<= target left)
       (<= target right)))

(defn local-max? [target left right]
  (and (> target left)
       (> target right)))

(defn safe-get-entry [m coll idx]
  (if (> m idx -1)
    (nth coll idx)
    0))

(defn get-candy-value
  [m rank-arr idx]
  (let [stored-candy-val (safe-get-entry m @candy-arr idx)
        get-entry (partial safe-get-entry m rank-arr)
        get-value (partial get-candy-value m rank-arr)]
    (cond
      (not (= stored-candy-val -1)) stored-candy-val

      (or (< idx 0) (>= idx m)) 0

      :else
      (let [itm (get-entry idx)
            left-idx (- idx 1)
            right-idx (+ idx 1)
            left (get-entry left-idx)
            right (get-entry right-idx)
            candy-val (cond ;; includes perfectly flat
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
        (swap! candy-arr assoc idx candy-val)
        candy-val))))

(defn candies [num coll]
  (doall (map-indexed (fn [idx itm] (get-candy-value num (vec coll) idx)) coll))
  (apply + @candy-arr))

(def result (candies n arr))
(spit fptr (str result "\n") :append true)
