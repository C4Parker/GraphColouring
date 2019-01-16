include "globals.mzn";

int: size; % Number of vertices in graph

set of int: DOMAIN = 1..size;
array[1..size,1..size] of 0..1 : adjacency;  % Adjacency matrix for graph

array[1..size] of var DOMAIN: graph; % Coloured vertices of graph

var int: numColours;

constraint forall(x,y in 1..size)
                 (adjacency[x,y] = 1 -> graph[x] != graph[y]); % No vertex colour can be same as any adjacent colours
constraint maximum(numColours, graph);

output ["Colours = ", show(graph), "\n", show(numColours)];

solve :: int_search(DOMAIN, smallest, indomain_min, complete)
         minimize(numColours);