import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with k colours.
*   Command line arguments: <DIMACS file location> <k-colours>
**/
public class MultiSum {
    
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
        int k = 7;         // Maximum number of colours to colour graph with
        
        // search upper/lower bound sequentially
        // actual solution would be multithreaded to find bounds in less time
        searchUpperBound(size, k);
        searchLowerBound(size, k);
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + timeTaken(startTime));
        
    }
    
    public static void searchUpperBound(int size, int numColours){
        Stack<Node> colouring = new Stack<Node>();
        boolean isColourable = true;
        int target = Integer.MAX_VALUE; // Default value, represents best cost colouring found
        
        while(isColourable){
            // Initialise domain of size k
            ArrayList<Node> domain = initialiseDomain(size, numColours);
            
            // Use target as upper bound on new colourings
            isColourable = search(domain, colouring, target, 0); 
            int sum = 0;
            int kCols = 0;
            for(Node n : colouring){
                sum += n.colour;
                if(n.colour > kCols)
                    kCols = n.colour;
            }
            if(sum <= target)
                target = sum;
            colouring.clear();  // Reset stack
            if(isColourable)
                System.out.println(sum + " cost "+ kCols+ "-colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms");
        }
    }
    
    public static void searchLowerBound(int size, int numColours){   
    
        Stack<Node> colouring = new Stack<Node>();
        boolean targetHit = false;
        int sum = Integer.MAX_VALUE; // Default value
        int upperBound = numColours * size / 2; // Represents all evenly sized colour classes, worst case sum colouring
        
        
        for(int target = size; target < upperBound && !targetHit; target++){
            // Initialise domain of size k
            ArrayList<Node> domain = initialiseDomain(size, numColours);
            targetHit = search(domain, colouring, target, 0); 
            
            sum = 0;
            int coloursUsed = 0;
            for(Node n : colouring){
                sum += n.colour;
                if(n.colour > coloursUsed)
                    coloursUsed = n.colour;
            }
            colouring.clear(); // Reset stack
            
            if(!targetHit)
                System.out.println("Optimal cost greater than " + target + "\t" + timeTaken(startTime));
            if(targetHit)
                System.out.println(sum + " cost "+ coloursUsed+ "-colouring found in " + timeTaken(startTime));
        }
    }
    
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring, int bestSum,int sum){
        if(domain.isEmpty())
            return sum < bestSum;
        
        if(domain.size() + sum >= bestSum)
            return false;
        
        Node d = pickNode(domain);
        //Node d = domain.get(0);        
        colourD:
        for(int colour : d.availableColours){
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
            if(search(newDomain, colouring, bestSum, sum+colour)){
                d.colour = colour;
                colouring.push(d);
                return true;
            }
        }
        return false;
    }
    

    
    public static Node pickNode(ArrayList<Node> domain){
        // DSATUR heuristic
        // chooses vertex with minimum available colours
        // tiebreaking on vertex cardinality(max wins)
        // first vertex found with only one available colour is coloured
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
    
    public static ArrayList<Node> initialiseDomain(int size, int numColours){
        ArrayList<Node> domain = new ArrayList<Node>(size);
        for(int i = 0; i < size; i++){
            ArrayList<Integer> colours = new ArrayList<Integer>(numColours);
            for(int j = 0; j < numColours; j++)
                colours.add(j+1);
            Node n = new Node();
            n.vertex = i;
            int nOrder = 0;
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    nOrder++;
            n.order = nOrder;
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