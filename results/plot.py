import matplotlib.pyplot as plt
import numpy as np

with open('pagerank.txt', 'r') as f:
    y = f.readlines()[0].split(',')

y[-1] = y[-1].strip('\n')
y = [float(x) for x in y]
x = range(100, 3000, 100)

plt.plot(x,y[:len(x)], color='purple', label='GraphX')


with open('pagerank-py.txt', 'r') as f:
    y = f.readlines()[0].split(',')

y[-1] = y[-1].strip('\n')
y = [float(x) for x in y]
x = range(100, 3000, 100)

plt.plot(x,y[:len(x)], color='red', label='Python')


plt.xlabel('Number Vertices')
plt.legend(loc="upper left")
plt.ylabel('Time (s)')
plt.title('PageRank')
plt.savefig('pagerank.png')