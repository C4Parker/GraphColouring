import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with k colours.
*   Command line arguments: <DIMACS file location> <k-colours>
**/
public class SumColourConstrained {
    
    // Hacky static declaration
    static boolean adjacency[][];
    static long startTime = java.lang.System.currentTimeMillis();
    
    public static void main(String args[]) throws IOException{
        
        // Parse input file and initialise graph
        String inputFilename = args[0];
        DIMACSInstance instance = Parser.parse(inputFilename);
        adjacency = instance.adjacencyMatrix;
        int size = instance.size;
        
        // preliminary variables
        int k = 5;         // Maximum number of colours to colour graph with
        
        // search
        search(size, k);
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + timeTaken(startTime));
        
    }
    
    public static void search(int size, int numColours){
        Stack<Node> colouring = new Stack<Node>();
        boolean isColourable = true;
        int target = Integer.MAX_VALUE; // Default value, represents best cost colouring found
        
        while(isColourable){
            // Initialise domain of size k
            ArrayList<Node> domain = initialiseDomain(adjacency);
            
            // Use target as upper bound on new colourings
            isColourable = search(domain, colouring, target, 0, 0); 
            int sum = 0;
            int coloursUsed = 0;
            for(Node n : colouring){
                sum += n.colour;
                if(n.colour > coloursUsed)
                    coloursUsed = n.colour;
            }
            if(sum <= target)
                target = sum;
            colouring.clear();  // Reset stack
            if(isColourable)
                System.out.println(sum + " cost "+ coloursUsed + "-colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms");
        }
    }
    
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring, int bestSum, int sum, int coloursUsed){
        if(domain.isEmpty())
            return sum < bestSum;
        if(domain.size() + sum >= bestSum)
            return false;
        Node d = pickNode(domain);
        
        colourD:
        for(int colour : d.availableColours){
            //if(colour <= coloursUsed + 1){
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]){
                        n.availableColours.remove(Integer.valueOf(colour));
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                //resolveAdjacency(newDomain);
                if(colour == coloursUsed + 1)
                    coloursUsed++;
                if(search(newDomain, colouring, bestSum, sum+colour, coloursUsed)){
                    d.colour = colour;
                    colouring.push(d);
                    return true;
                }
            //}
        }
        return false;
    }
    
    // Not great heuristic when domain is variable size for each vertex, thinks vertices with enormous order are easy to colour
    /**
     * DSATUR heuristic
     * Chooses vertex(node) with minimum available colours, tiebreaking on vertex degree.
     * First vertex found with only one available colour is automatically chosen.
    **/
    public static Node pickNode(ArrayList<Node> domain){
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        int maxOrder = d.order;
        for(Node n : domain){
            int nColours = n.availableColours.size();
            if (nColours <= dColours){
                if(nColours == dColours && n.order > maxOrder)
                    maxOrder = n.order;
                d = n;
                dVertex = n.vertex;
                dColours = n.availableColours.size();
            }
            if(nColours == 1)
                break;
        }
        return d;
    }
    
    
    /**
     * Initialise domain of size, with d+1 colours for vertex of degree d
    **/
    public static ArrayList<Node> initialiseDomain(boolean[][] adjacency){
        int size = adjacency.length;
        ArrayList<Node> domain = new ArrayList<Node>(size);
        for(int i = 0; i < size; i++){
            Node n = new Node();
            n.vertex = i;
            
            int nOrder = 0;
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    nOrder++;
            n.order = nOrder;
            
            ArrayList<Integer> colours = new ArrayList<Integer>(nOrder + 1);
            for(int j = 1; j <= nOrder + 1; j++)
                colours.add(j);
            n.availableColours = colours;
            
            n.colour = null;
            domain.add(n);
        }
        return domain;
    }
    
    public static String timeTaken(long startTime){
        return (java.lang.System.currentTimeMillis() - startTime) + "ms";
    }
    
}