# Finds the degree centrality of a given graph
import networkx as nx
import sys
import operator
import matplotlib.pyplot as plt
import time
import numpy as np

def degree_centrality(G):
    centrality = {}
    for node in G.nodes:
        centrality[node] = G.degree[node]
    
    centrality = sorted(centrality.items(), key=operator.itemgetter(1), reverse=True)

    return centrality

# creates a graph of tab delimited edges listed line by line
def create_graph(fname):
    # open and read in graph
    f = open(fname, 'r')
    lines = f.readlines()
    f.close()

    G = nx.Graph()

    # create Graph
    for line in lines[1:]:
        a, b = line.strip("\n").split("\t")
        G.add_edge(a, b)
    return G

times = []

x = np.arange(5, 5000, 100)

for i in range(5, 5000, 100):
    G =  nx.dense_gnm_random_graph(i, i*i)
    print i
    start_time = time.time()
    degree_centrality(G)
    times.append((time.time() - start_time)*1000)

plt.scatter(x, times)

z = np.polyfit(x, times, 2)
p = np.poly2d(z)
plt.plot(x,p(x),"r--")

plt.xlabel('Number Vertices')
plt.ylabel('Time (ms)')
plt.title('Degree Centrality: Dense Undirected')
plt.savefig('degree-dense.png')