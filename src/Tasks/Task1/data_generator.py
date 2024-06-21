import numpy as np

size = 1000000
path = "./input_array.txt"

a = np.random.randint(size * 10, size = size)
with open(path, "w") as file:
    file.write(str(size) + '\n')
    for i in a:
        file.write(str(i) + ' ')