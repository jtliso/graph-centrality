import networkx as nx

for i in range(8300, 1000000, 100):
	print i
	G = nx.fast_gnp_random_graph(i, .3)
	nx.write_edgelist(G, str(i)+'.el', delimiter='\t', data=False)

