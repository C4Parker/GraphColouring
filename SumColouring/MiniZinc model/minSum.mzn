include "globals.mzn";

int: strength = size; % Upper bound strength of graph
int: chi; % Lower bound strength of graph

set of int: DOMAIN = 1..strength;

int: size; % Number of vertices in graph
array[1..size,1..size] of 0..1 : adjacency;  % Adjacency matrix for graph

% set of int: 1..size = 1..size;
array[1..size] of var DOMAIN: graph; % Coloured graph
constraint forall(x,y in 1..size)
                 (adjacency[x,y] = 1 -> graph[x] != graph[y]);

% Standard graph colouring constraint and cost function
var int: cost;
constraint cost = sum(graph);

% Maximum weight of vertex is its cardinality + 1
array[1..size] of var 1..size: vOrder;
constraint forall(v in vOrder)
                 (sum (i in 1..size)((adjacency[v,i] = 1)) = v);
constraint forall(v in graph)(v <= vOrder[v]+1);


% Strictly order colour classes so that largest is given lowest weight, smallest is given largest weight
array[DOMAIN] of var int: classSizes; 
constraint forall(i in 1..strength)
                 (count(graph, i, classSizes[i]));
constraint forall(i in 1..strength-1)
                 (classSizes[i] >= classSizes[i+1]);
                 
                 
array[1..size, DOMAIN] of var int: inClass; % 2D array, each row represents a colour class, 1 for that vertex in class, 0 for not in that class
                                             % Same as graph with different representation
constraint forall(vertex in 1..size, colour in DOMAIN)(graph[vertex] == colour -> inClass[vertex,colour] = 1);
constraint forall(vertex in 1..size, colour in DOMAIN)(graph[vertex] != colour -> inClass[vertex,colour] = 0);

constraint forall(i in 1..strength-1)(classSizes[i] == classSizes[i+1] -> lex_lesseq(row(inClass,i),row(inClass,i+1)));

% Redundant constraint, all colour classes less than or equal to colouring number must have some vertices with that colour
constraint forall(i in 1..chi)(classSizes[i] >= 1);

%constraint cost >= size;
constraint cost >= (floor(chi*(chi+1)/2 - 1)) + (size-chi);
constraint cost <= ceil(chi/2) * size;



output ["Colours = ", show(graph),"\nClassSizes = ", show(classSizes), "\nCost = ", show(cost)];

solve :: int_search(DOMAIN, smallest, indomain_min, complete)
      minimize(cost);