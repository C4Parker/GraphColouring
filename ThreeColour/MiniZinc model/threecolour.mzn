enum COLOUR = {Red, Green, Blue};

int: vertices; % Number of vertices in graph
array[size,size] of bool : adjacency;  % Adjacency matrix for graph

set of int: size = 1..vertices;
array[size] of var COLOUR: graph; % Coloured vertices of graph


constraint forall(x,y in size)
                 (adjacency[x,y] -> graph[x] != graph[y]); % No vertex colour can be same as any adjacent colours

output ["Colours = ", show(graph), "\n", show(adjacency)];

solve satisfy;