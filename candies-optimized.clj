(def fptr (get (System/getenv) "OUTPUT_PATH"))

(def n (Integer/parseInt (clojure.string/trim (read-line))))

(def arr (reduce (fn [coll val] (conj coll (Integer/parseInt (read-line))))
                 [] (range n)))

(def candy-arr (atom (vec (take n (repeat 0)))))

(defn set-candy
  "Set an `idx` in `candy-arr` to `val`"
  [idx val] (swap! candy-arr assoc idx val))

(defn get-candy-at
  "Get the entry at `idx` in `candy-arr`"
  [idx] (nth @candy-arr idx))

;; handle edge cases outside of function
(defn find-candy-at
  "Find (calculate) the number of candies at `idx`"
  [idx]
  (let [left-idx (- idx 1)
        right-idx (+ idx 1)
        curr (nth arr idx)
        left (nth arr left-idx)
        right (nth arr right-idx)]
    (cond
      ;; local minimum
      (and (<= curr left) (<= curr right))
      1
      ;; local maximum
      (and (> curr left) (> curr right))
      (do
        ;; neighbors needed and not calculated. Calculate them
        (when (= 0 (get-candy-at left-idx))
          (set-candy left-idx (find-candy-at left-idx)))
        (when (= 0 (get-candy-at right-idx))
          (set-candy right-idx (find-candy-at right-idx)))
        ;; both neighbors now calculated, increment their max
        (+ 1 (max (get-candy-at left-idx)
                  (get-candy-at right-idx))))
      ;; ascending
      (> curr left)
      (do
        ;; left neighbor needed and not calculated. Calculate it
        (when (= 0 (get-candy-at left-idx))
          (set-candy left-idx (find-candy-at left-idx)))
        ;; increment left
        (+ 1 (get-candy-at left-idx)))
      ;; descending
      (> curr right)
      (do
        ;; right neighbor needed and not calculated. Calculate it
        (when (= 0 (get-candy-at right-idx))
          (set-candy right-idx (find-candy-at right-idx)))
        ;; increment right
        (+ 1 (get-candy-at right-idx))))))


(defn candies [& _]  ; no point passing args if they're global defined...
  ;; leftmost index is smaller than right neighbor
  (when (<= (nth arr 0) (nth arr 1))
    (set-candy 0 1))
  ;; rightmost index is smaller than left neighbor
  (when (<= (nth arr (- n 1)) (nth arr (- n 2)))
    (set-candy (- n 1) 1))
  ;; leftmost index is larger than right neighbor
  (when (> (nth arr 0) (nth arr 1))
    ;; right plus one
    (set-candy 0 (+ 1 (find-candy-at 1))))
  ;; rightmost index is larger than left neighbor
  (when (> (nth arr (- n 1)) (nth arr (- n 2)))
    ;; left plus one
    (set-candy (- n 1) (+ 1 (find-candy-at (- n 2)))))
  ;; calculate all indexes that aren't on the ends
  (doseq [idx (range 1 (- n 1))]
    (when (= 0 (get-candy-at idx))
      (set-candy idx (find-candy-at idx))))
  ;; summation
  (apply + @candy-arr))


(def result (candies n arr))
(spit fptr (str result "\n") :append true)
