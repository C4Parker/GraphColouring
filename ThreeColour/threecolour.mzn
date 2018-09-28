enum COLOUR = {red, green, blue};

int: vertices; % Number of vertices in graph
int: minEdges; % Minimum number of points for generating random graph

set of int: size = 1..vertices;

array[size] of var COLOUR: graph; % Coloured vertices of graph
array[size,size] of var bool : adjacency;  % Adjacency matrix for graph

/* Builds adjacency matrix for graph */

constraint sum(adjacency) >= minEdges; % 
constraint forall(x in size)(adjacency[x,x] = 0); % Removes 1-cycles from graph
constraint forall(x,y in size)(adjacency[x,y] == adjacency[y,x]); % Ensures adjacency matrix is symmetric

constraint forall(x,y in size)
                 (adjacency[x,y] -> graph[x] != graph[y]); % No vertex colour can be same as any adjacent colours

%output ["Colours = ", show(graph)]; %, "\nAdjacency = ", show(adjacency)];

solve satisfy;