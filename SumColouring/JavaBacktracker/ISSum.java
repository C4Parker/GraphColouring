import java.io.*;
import java.util.*;

/**
*   Sum colours graph G by finding large independent sets within the graph
*   Command line arguments: <DIMACS file location>
**/
public class ISSum {
    
    // Hacky static declaration
    static boolean adjacency[][];
    
    public static void main(String args[]) throws IOException{
        
        // Parse input file and initialise graph
        String inputFilename = args[0];
        DIMACSInstance instance = Parser.parse(inputFilename);
        adjacency = instance.adjacencyMatrix;
        int size = instance.size;
        
        long startTime = java.lang.System.currentTimeMillis();
        
        // Initialise stack
        Stack<Node> colouring = new Stack<Node>();
        
        // Initialise domain
        ArrayList<Node> domain = new ArrayList<Node>();
        for(int i = 0; i < size; i++)
            domain.add(new Node(i, null));
        
        int sum = search(domain, colouring, 0);
        
        int colours = 0;
        for(Node n : colouring)
            if(n.colour > colours)
                colours = n.colour;
            
        System.out.println("Colours: " + colours + "\tSum: " + sum);
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
    }
    
    public static int search(ArrayList<Node> domain, Stack<Node> colouring, int numColours){
        if(domain.isEmpty())
            return calcSum(colouring);
		// Let H be maximum independent set of G
        ArrayList<Node> maxIS = maxIS(domain);
		
		
		
		// Let G' be G \ H
		// Assign all v in H to next smallest colour
        numColours++;
		for(Node n : maxIS){
			n.colour = numColours;
			domain.remove(n);
			colouring.push(n);
		}
		return search(domain, colouring, numColours);
		
    }
	
    public static int calcSum(Stack<Node> colouring){
        int sum = 0;
        for(Node n : colouring)
            sum += n.colour;
        return sum;
    }
    
    // Returns a maximal independent set
	public static ArrayList<Node> maxIS(ArrayList<Node> graph){
		// Given graph G(V, E)
		// Let I be an empty set
		ArrayList<Node> maxIS = new ArrayList<Node>();
		// While G is not empty
		while(!graph.isEmpty()){
			// Chose a node v in V
			Node n = pickNode(graph);
			
			// Add v to the set I
			maxIS.add(n);
			// Remove v and all adjacent vertices from G 
            
            ArrayList<Node> newGraph = new ArrayList<Node>();
			for(Node v : graph)
				if(!adjacency[v.vertex][n.vertex] && v.vertex != n.vertex)
					newGraph.add(v);
            graph = newGraph;
		}
		// Return I
		return maxIS;
	}
    
        public static Node pickNode(ArrayList<Node> domain){
        // chooses vertex with minimum cardinality
        Node d = domain.get(0);
        int minOrder = d.order;
        for(Node n : domain){
            if (n.order < minOrder){
                minOrder = n.order;
                d = n;
            }
            if(n.order <= 1)
                break;
        }
        return d;
    }
	
}


















