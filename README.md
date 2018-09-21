# hackerrank-candies
Attempts at HackerRank's Candies problem

## Problem Statement

Alice is a kindergarten teacher. She wants to give some candies to the children
in her class.  All the children sit in a line and each of them has a rating
score according to his or her performance in the class.  Alice wants to give at
least 1 candy to each child. If two children sit next to each other, then the
one with the higher rating must get more candies. Alice wants to minimize the
total number of candies she must buy.

For example, assume her students' ratings are [4, 6, 4, 5, 6, 2]. She gives the
students candy in the following minimal amounts: [1, 2, 1, 2, 3, 1]. She must
buy a minimum of 10 candies.

## Technique

First I figured out what the various cases are:
1. Child has a higher score than left
1. Child has a higher score than right
1. All of the above
1. None of the above

The only case which isn't based on a peer is when the child has a lower score
than both neighbors, so this is our base case. Each other case must recursively
calculate the neighbor children's candy values, until it finds a neighbor who
is a local minimum, our base case.

Performing this operation from scratch with each child is going to be very
expensive, so any time a value is determined it gets stored in a vector of candy
values for each child.

The procedure, when mapped over a collection, will traverse from left to right
and calculate each candy value by recursively scanning for a local minimum and
incrementing its way back up the stack to the called index. Any elements that
were calculated in this procedure are stored and can then get retrieved without
calculation when their index comes.

Given [9 8 7] and asked to calculate idx 0 with rank 9

1. [0 0 0]  ; initial values
1. [(+ 1 (candy-val 1)) 0 0]  ; idx 0 based on idx 1
1. [(+ 1 (candy-val 1)) (+ 1 (candy-val 2)) 0]  ; idx 1 based on idx 2
1. [(+ 1 (candy-val 1)) (+ 1 (candy-val 2)) 1]  ; idx 2 base case
1. [(+ 1 (candy-val 1)) (+ 1 1) 1]  ; idx 2 has a value now
1. [(+ 1 (candy-val 1)) 2 1]  ; calculate idx 1
1. [(+ 1 2) 2 1]  ; idx 1 has a value now
1. [3 2 1]  ; calculate idx 0

The ascending case is trivial in that the left side will start at a value of 1
and the subsequent entries will just be increments on the previous.


## Initial

My initial implementation failed most test cases marked with a timeout. I'll
describe what I did here and the following section will describe where I think I
went wrong, and my corrections to those mistakes.

Following HackerRank's template, I created a `candies` function which accepted
the size of the rank array, and the rank array. I refrained from using the
global defines for these in adherence to their format. This function simply maps
the `get-candy-value` for an index function over the rank array, and applies a
summation over the calculated `candy-arr`, which tracks how many candies each
child will receive.

I initially tried to write the program to generate the list recursively without
storing in an atom which had to be modified as a side-effect, which is counter
to the way Clojure is typically written, but I wasn't able to figure out a good
way to do that without loads of recalculating.

The `get-candy-value` function in this first iteration accepted as parameters
the size of the rank array, the rank array itself, and the index to calculate.
It handles three conditions:
1. the candy value is already stored and simply returns that value
1. the index requested is out of bounds and we assume a value of 0
1. value is not stored and index is in bounds, so the value must be calculated

I'll get to the weird zero value in a moment.

The calculation for the value is fairly straightforward after the edge cases.
If the index has a local minimum rank, base case of 1. If it's a local max,
increment from its higher neighbor. If it's larger than the left and smaller
than right, increment from its left. The opposite holds as well. Once it's
calculated, store in the `candy-arr` atom.

There are three helper functions for calculating `local-min?`, `local-max?`,
and a `safe-get-entry`. This last function handles the case when a starting or
ending index requests its neighbors, which would normally result in an
IndexOutOfBoundsException, and instead returns a value of 0. So when an out of
bounds candy value is requested, it's assuemd to be zero. When an out of bounds
rank is requested, it's assumed the child earned a zero on their test. Their
neighbor would be higher and increment the candy value, which is zero, to one.
Boom. Base case. It's a weird and gross way of handling this but it's what grew
naturally as I was writing this on the first pass. On a second look, since the
candy value is acquired this way, I could have eliminated the out of bounds case
mentioned in the `get-candy-value` function by assuming a default candy-val of 0
instead of -1. Whoops.

So, despite the weirdness of the implementation, this performs the calculations
I intended and worked for the base test cases HackerRank provides before
submission, but failed basically the rest of them for the stated reason of a
time-out. I think there's probably an off by one in the index logic somewhere.


## Optimized

The main difference between this attempt and the first attempt is moving the
edge-case logic up from `get-candy-value` (now `find-candy-at`) to `candies`.
In addition, retrieving an already-calculated value is separated from doing a
new calculation and explicitly handled before the retrieval happens. This
prevents a lot of unnecessary recurses.

The `candies` function now, instead of simply mapping the calculation function
and summing the `candy-arr`, handles setting the leftmost and rightmost elements
as literal edge-cases. In addition, instead of mapping over every element in the
sequence, loop over the indices and skip any that have already been calculated.
Finally, sum over the results as before.

The last change I made I'm not sure if it even helped or not, but the way that
HackerRank was generating `arr` by re-`def`ing with each line is a pretty bad
way to do it, so I replaced that with a proper `reduce`.
