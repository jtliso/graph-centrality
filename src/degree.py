# Finds the degree centrality of a given graph
import networkx as nx
import sys
import operator

def degree_centrality(G):
    centrality = {}
    for node in G.nodes:
        centrality[node] = G.degree[node]
    
    centrality = sorted(centrality.items(), key=operator.itemgetter(1), reverse=True)

    return centrality

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

print degree_centrality(G)