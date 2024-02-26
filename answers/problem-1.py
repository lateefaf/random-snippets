def solution(input):
    integers = []
    strings = []
    specials = []
    for item in input:
        if isinstance(item, int):
            integers.append(item)
        elif item.isdigit():
            integers.append(int(item))
        elif item.isalpha():
            strings.append(item)
        else:
            specials.append(item)

    print(integers)
    print(strings)
    print(specials)


input = ['A', 'B', 'C', 1, 2, 3, '4', '5', 6, '@', '~', 'D']
solution(input)

# 1. input = [ 'A', 'B','C', 1, 2, 3, '4', '5', 6, '@', '~', 'D' ]
#
# * Split the string, integer, and special characters into new arrays.
# * Make sure 4 and 5 which is a string should be in integer array list and convert it to integer.
#
# Console Output:
# integer = [1,2,3,4,5,6]
# string = [ 'A', 'B', 'C', 'D' ]
# chars = [ '@', '~' ]
