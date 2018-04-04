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

G = create_graph('../data/bio-yeast.el')
print nx.eigenvector_centrality_numpy(G)