set of int: DOMAIN = size;% = to_enum(size);%{1..vertices};

int: vertices; % Number of vertices in graph
array[size,size] of bool : adjacency;  % Adjacency matrix for graph

set of int: size = 1..vertices;
array[size] of var DOMAIN: graph; % Coloured vertices of graph
var int: cost;

constraint forall(x,y in size)
                 (adjacency[x,y] -> graph[x] != graph[y]); % No vertex colour can be same as any adjacent colours
constraint cost = sum(graph);

output ["Colours = ", show(graph), "\nCost = ", show(cost)];

solve minimize(cost);