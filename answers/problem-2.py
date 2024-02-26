import random
from random import shuffle


def solution(input):
    shuffle(input)
    half = len(input) // 2

    array_one = input[:half]
    array_two = input[half:]

    sum_one = sum(array_one)
    sum_two = sum(array_two)

    print(f"first array = {array_one}")
    print(f"second array = {array_two}")
    print(f"first sum = {sum_one}")
    print(f"second sum = {sum_two}")


def solutionTwo(input):
    shuffle(input)
    size = random.randint(1, len(input))
    print(f"size is {size}")
    array_one = input[:size]
    array_two = input[size:]

    sum_one = sum(array_one)
    sum_two = sum(array_two)

    print(f"first array = {array_one}")
    print(f"second array = {array_two}")
    print(f"first sum = {sum_one}")
    print(f"second sum = {sum_two}")


input = [1, 2, 3, 4, 5, 6]
inputTwo = [1, 2, 3, 4, 5, 6, 7]
solution(input)
solutionTwo(inputTwo)
