# Finds the eigenvector centrality of a given graph
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

x = np.arange(5, 100005, 1000)

for i in range(5, 100005, 1000):
    G =  nx.gnm_random_graph(i, i*i)
    print i
    start_time = time.time()
    nx.eigenvector_centrality_numpy(G)
    times.append(time.time() - start_time)

plt.scatter(x, times)

z = np.polyfit(x, times, 2)
p = np.poly1d(z)
plt.plot(x,p(x),"r--")

plt.xlabel('Number Vertices')
plt.ylabel('Time (ms)')
plt.title('Eigenvector Centrality: Dense Undirected')
plt.savefig('eigen-dense.png')