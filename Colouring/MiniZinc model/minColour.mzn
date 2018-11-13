include "globals.mzn";

set of int: DOMAIN = 1..vertices;
int: vertices; % Number of vertices in graph
array[size,size] of bool : adjacency;  % Adjacency matrix for graph

set of int: size = 1..vertices;
array[size] of var DOMAIN: graph; % Coloured vertices of graph

var int: numColours;

constraint forall(x,y in size)
                 (adjacency[x,y] -> graph[x] != graph[y]); % No vertex colour can be same as any adjacent colours
constraint maximum(numColours, graph);

output ["Colours = ", show(graph), "\n", show(numColours)];

solve :: int_search(DOMAIN, smallest, indomain_min, complete)
         minimize(numColours);