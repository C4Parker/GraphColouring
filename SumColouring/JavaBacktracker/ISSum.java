import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with k colours.
*   Command line arguments: <DIMACS file location>
**/
public class ISSum {
    // Initialise default values for graph
    static int size = -1;
    static boolean adjacency[][] = new boolean[1][1];
    
    public static void main(String args[]) throws IOException{
        
        String inputFilename = args[0];
        
        
        // Read in data from file
        try{
        FileReader reader = new FileReader(inputFilename);
        Scanner in = new Scanner(reader);
        String line = in.nextLine();
        
            while(line != null){
                String symbols[] = line.split(" ");
                if(symbols[0].equals("p")){
                    size = Integer.parseInt(symbols[2]);
                    adjacency = new boolean[size][size];
                }else if(symbols[0].equals("e")){
                    int v1 = Integer.parseInt(symbols[1]);
                    int v2 = Integer.parseInt(symbols[2]);
                    adjacency[v1-1][v2-1] = true;
                }
                line = in.nextLine();
            }
            System.out.println(line);
        in.close();
        }catch(Exception ex){}
        
        // Remove edges connecting vertex to itself
        for(int i = 0; i < size; i++)
            adjacency[i][i] = false;
        
        // Make adjacency matrix symmetric
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    adjacency[j][i] = true;   
        
        // Find upper bound colouring for graph
        int maxOrder = 1;
        for(int i = 0; i < size; i++){
            int iOrder = 0;
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    iOrder++;
            if(iOrder > maxOrder)
                maxOrder = iOrder;
        }
        
        
        
        
        long startTime = java.lang.System.currentTimeMillis();
        
        // preliminary variables
        boolean isColourable = true;
        int bestSum = Integer.MAX_VALUE;
        Stack<Node> colouring = new Stack<Node>();
        
        // Initialise domain
        ArrayList<Node> domain = new ArrayList<Node>();
        for(int i = 0; i < size; i++)
            domain.add(new Node(i, null));
        
        int sum = search(domain, colouring, 0);
        
        System.out.println(sum);
        
        
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
    
	public static ArrayList<Node> maxIS(ArrayList<Node> graph){
		// Given graph G(V, E)
		// Let I be an empty set
		ArrayList<Node> maxIS = new ArrayList<Node>();
		// While G is not empty
		while(!graph.isEmpty()){
			// Chose a node v in V
			Node n = graph.get(0);
			
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
	
}

class Node{
	public int vertex;
    public Integer colour;
    
    public Node(int vertex, Integer colour){
        this.vertex = vertex;
        this.colour = colour;
    }
    public Node(){
    }
    
    /*@Override
    public Node clone(){
        Node clone = new Node();
        clone.vertex = this.vertex;
        clone.order = this.order;
        clone.availableColours = new ArrayList<Integer>();
        for(int c : this.availableColours)
            clone.availableColours.add(c);
        
        return clone;
    }*/
    
}























