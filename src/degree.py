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
    fname = sys.argv[1]
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

for i in range(5, 10005, 10):
    G =  nx.gnm_random_graph(i, 2*i)
    print i
    start_time = time.time()
    degree_centrality(G)
    times.append(time.time() - start_time)

plt.scatter(np.arange(5, 10005, 10), times)
plt.show()