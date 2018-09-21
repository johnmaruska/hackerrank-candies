#!/bin/python3
import os


def find_candy_at(scores_arr, candies_arr, idx):
    """Find (calculate) the number of candies at `idx`"""
    left_idx = idx - 1
    right_idx = idx + 1
    curr = scores_arr[idx]
    left = scores_arr[left_idx]
    right = scores_arr[right_idx]
    # local minimum
    if curr <= left and curr <= right:
        return 1
    # local maximum
    elif curr > left and curr > right:
        # neighbors needed and not calculated. Calculate them
        if candies_arr[left_idx] == 0:
            candies_arr[left_idx] = find_candy_at(scores_arr,
                                                  candies_arr,
                                                  left_idx)
        if candies_arr[right_idx] == 0:
            candies_arr[right_idx] = find_candy_at(scores_arr,
                                                   candies_arr,
                                                   right_idx)
        return 1 + max(candies_arr[left_idx], candies_arr[right_idx])
    # ascending
    elif curr > left:
        # left neighbor needed and not calculated. Calculate it
        if candies_arr[left_idx] == 0:
            candies_arr[left_idx] = find_candy_at(scores_arr,
                                                  candies_arr,
                                                  left_idx)
        return 1 + candies_arr[left_idx]
    # descending
    elif curr > right:
        if candies_arr[right_idx] == 0:
            candies_arr[right_idx] = find_candy_at(scores_arr,
                                                   candies_arr,
                                                   right_idx)
        return 1 + candies_arr[right_idx]


# Complete the candies function below.
def candies(n, arr):
    candies_arr = [0] * n

    # leftmost index is smaller than right neighbor
    if arr[0] <= arr[1]:
        candies_arr[0] = 1
    # rightmost index is smaller than left neighbor
    if arr[n-1] <= arr[n-2]:
        candies_arr[n-1] = 1

    # calculate all indices that aren't on the ends
    for idx in range(1, n-1):
        if candies_arr[idx] == 0:
            candies_arr[idx] = find_candy_at(arr, candies_arr, idx)

    # leftmost index is larger than right neighbor
    if arr[0] > arr[1]:
        # right plus one
        candies_arr[0] = 1 + find_candy_at(arr, candies_arr, 1)
    # rightmost index is larger than left neighbor
    if arr[n-1] > arr[n-2]:
        # left plus one
        candies_arr[n-1] = 1 + find_candy_at(arr, candies_arr, n-2)

    return sum(candies_arr)


if __name__ == '__main__':
    fptr = open(os.environ['OUTPUT_PATH'], 'w')

    n = int(input())

    arr = []

    for _ in range(n):
        arr_item = int(input())
        arr.append(arr_item)

    result = candies(n, arr)

    fptr.write(str(result) + '\n')

    fptr.close()
