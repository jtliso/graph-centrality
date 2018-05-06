# Finds the eigenvector centrality of a given graph
import networkx as nx
import sys
import operator
import time
import numpy as np

e_times = []
c_times = []
d_times = []
p_times = []

for i in range(100, 500, 100):
    print i
    G = nx.read_edgelist("../data/random/"+str(i)+".el")

    start_time = time.time()
    nx.eigenvector_centrality_numpy(G)
    e_times.append(time.time() - start_time)
    
    start_time = time.time()
    nx.degree_centrality(G)
    d_times.append(time.time() - start_time)

    
    start_time = time.time()
    nx.pagerank(G)
    p_times.append(time.time() - start_time)

    
    start_time = time.time()
    nx.closeness_centrality(G)
    c_times.append(time.time() - start_time)

print e_times
print c_times
print d_times
print p_times
