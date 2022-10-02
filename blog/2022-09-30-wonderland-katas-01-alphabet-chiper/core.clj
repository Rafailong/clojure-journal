(ns core
  (:require [clojure.string :as str]))

;; for this kata there are 3 given pieces of data:
(def alphabet "abcdefghijklmnopqrstuvwxyz")
(def keyword "scones")
(def message "meetmeatthetree")

;; there are 2 important things we need to know before
;; trying to solve this kata. those are:
;; 1 - zip (pair) two collections' elements in pairs in order to get
;;     something like:
;;     [(s m) (c e) (o e) (n t) (e m) (s e)...]
;; 2 - rotate alphabet in a given char, meaning, generate
;;     a copy of alphabet starting in a given char i.e.
;;     alphabet starting in 'm' would be:
;;     mnopqrstuvwxyzabcdefghijkl

;; the way I know we can zip collection together in clojure is using 
;; the map function
(map vector keyword message) ;; ([\s \m] [\c \e] [\o \e] [\n \t] [\e \m] [\s \m])
;; but this method has the same issue as the Scala zip function
;; it will stop at the end of the shortest string/collection

;; so, i need to create a collection of repeating keywords
;; so much less code that in Scala
(def infinite-keyword
  (-> keyword
      seq
      cycle))

;; it seem that our infinite-keyword works as expected
(take 10 infinite-keyword) ;; (\s \c \o \n \e \s \s \c \o \n)
(map vector infinite-keyword message)
;; ([\s \m]
;;  [\c \e]
;;  [\o \e]
;;  [\n \t]
;;  [\e \m]
;;  [\s \e]
;;  [\s \a]
;;  [\c \t]
;;  [\o \t]
;;  [\n \h]
;;  [\e \e]
;;  [\s \t]
;;  [\s \r]
;;  [\c \e]
;;  [\o \e])

;; now, it is time for our function to shift out alphabet
(defn alphabet-starting-at [c]
  (->> c
       (str/index-of alphabet)
       (#(split-at % alphabet))
       (map #(str/join "" %))
       reverse
       (str/join "")))
(assert (= (alphabet-starting-at \d) "defghijklmnopqrstuvwxyzabc"))
;; it seems that alphabet-starting-at works as expected

;; let's build our matrix
(def matrix
  (->> alphabet
       seq
       (map alphabet-starting-at)))
;; (
;; abcdefghijklmnopqrstuvwxyz
;; bcdefghijklmnopqrstuvwxyza
;; cdefghijklmnopqrstuvwxyzab
;; defghijklmnopqrstuvwxyzabc
;; efghijklmnopqrstuvwxyzabcd
;; fghijklmnopqrstuvwxyzabcde
;; ghijklmnopqrstuvwxyzabcdef
;; hijklmnopqrstuvwxyzabcdefg
;; ijklmnopqrstuvwxyzabcdefgh
;; jklmnopqrstuvwxyzabcdefghi
;; klmnopqrstuvwxyzabcdefghij
;; lmnopqrstuvwxyzabcdefghijk
;; mnopqrstuvwxyzabcdefghijkl
;; nopqrstuvwxyzabcdefghijklm
;; opqrstuvwxyzabcdefghijklmn
;; pqrstuvwxyzabcdefghijklmno
;; qrstuvwxyzabcdefghijklmnop
;; rstuvwxyzabcdefghijklmnopq
;; stuvwxyzabcdefghijklmnopqr
;; tuvwxyzabcdefghijklmnopqrs
;; uvwxyzabcdefghijklmnopqrst
;; vwxyzabcdefghijklmnopqrstu
;; wxyzabcdefghijklmnopqrstuv
;; xyzabcdefghijklmnopqrstuvw
;; yzabcdefghijklmnopqrstuvwx
;; zabcdefghijklmnopqrstuvwxy
;; )

;; in the scala solution i did not use the matrix, i only generated it
;; for this solution i will use it, so, a function to find the alphabet
;; for a given letter is needed.
;; given that the find function in clojure is only stable for maps
;; i will use the following function
(defn collumn-for [c]
  (first (for [row matrix :when (str/starts-with? row (str c))] row)))

;; the encoding function is pretty similar to the one in the scala solution
(defn encode [secret message]
  (let [key (infinite-secret secret)
        pairs (map vector key message)
        encode-char (fn [pair]
                      (let [[k, c] pair
                            index-of-c (str/index-of alphabet c)]
                        (nth (collumn-for k) index-of-c)))]
    (str/join "" (map encode-char pairs))))

(defn decode [keyword message]
  "decodeme")

(defn decipher [cipher message]
  "decypherme")
