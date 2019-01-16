include "globals.mzn";

int: strength = 10; % magic number, upper bound strength of graph

set of int: DOMAIN = 1..strength;

int: size; % Number of size in graph
array[1..size,1..size] of 0..1 : adjacency;  % Adjacency matrix for graph

% set of int: 1..size = 1..size;
array[1..size] of var DOMAIN: graph; % Coloured size of graph
array[1..size] of var 1..size: vOrder;
var int: cost;
array[DOMAIN] of var int: classSizes; 

% Standard graph colouring constraint and cost function
constraint forall(x,y in 1..size)
                 (adjacency[x,y] = 1 -> graph[x] != graph[y]);
constraint cost = sum(graph);

% Maximum weight of vertex is its cardinality + 1
constraint forall(v in vOrder)
                 (sum (i in 1..size)((adjacency[v,i] = 1)) = v);
constraint forall(v in graph)(v <= vOrder[v]+1);


% Strictly order colour classes so that largest is given lowest weight, smallest is given largest weight
constraint forall(i in 1..strength)
                 (count(graph, i, classSizes[i]));
constraint forall(i in 1..strength-1)
                 (classSizes[i] >= classSizes[i+1]);
                 
                 
%constraint cost >= size;
%constraint cost >= maxClique*(maxClique-2)  + size;
%constraint cost <= (maxClique/2) * size;
%constraint class1..sizes[maxClique] >= 1;



output ["Colours = ", show(graph),"\nClassSizes = ", show(classSizes), "\nCost = ", show(cost)];

solve :: int_search(DOMAIN, smallest, indomain_min, complete)
      minimize(cost);