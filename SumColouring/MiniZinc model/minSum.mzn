include "globals.mzn";

int: strength = 9; % magic number, upper bound strength of graph

set of int: DOMAIN = 1..strength;

int: vertices; % Number of vertices in graph
array[size,size] of bool : adjacency;  % Adjacency matrix for graph

set of int: size = 1..vertices;
array[size] of var DOMAIN: graph; % Coloured vertices of graph
array[size] of var size: vOrder;
var int: cost;
array[DOMAIN] of var int: classSizes; 

% Standard graph colouring constraint and cost function
constraint forall(x,y in size)
                 (adjacency[x,y] -> graph[x] != graph[y]);
constraint cost = sum(graph);

% Maximum weight of vertex is its cardinality + 1
constraint forall(v in vOrder)
                 (sum (i in size)((adjacency[v,i] = true)) = v);
constraint forall(v in graph)(v <= vOrder[v]+1);


% Strictly order colour classes so that largest is given lowest weight, smallest is given largest weight
constraint forall(i in 1..strength)
                 (count(graph, i, classSizes[i]));
constraint forall(i in 1..strength-1)
                 (classSizes[i] >= classSizes[i+1]);


output ["Colours = ", show(graph),"\nClass sizes = ", show(classSizes), "\nCost = ", show(cost)];

solve :: int_search(DOMAIN, smallest, indomain_min, complete)
      minimize(cost);