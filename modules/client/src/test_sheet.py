x = {'a': 1, 'b': 2, 'c': 2}
l = [1, 2]
a = [1, 2]
b = [3, 4]
c = [5, 6]
print(any([z >= 2 for z in x.values()]))

# print(next([x for x in x.items()]))

print(max(x.keys(), key=(lambda key: x[key])))

print(list(zip(l, l)))
print(list(zip(l, l)))
print(list(zip(list(zip(l, l)), l)))
print([{'a': a, 'b': b, 'c': c} for (a, b), c in zip(list(zip(a, b)), c)])
