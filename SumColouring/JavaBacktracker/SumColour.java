import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with k colours.
*   Command line arguments: <DIMACS file location> <k-colours>
**/
public class SumColour {
    
    // Hacky static declaration
    static boolean adjacency[][];
    
    public static void main(String args[]) throws IOException{
        
        // Parse input file and initialise graph
        String inputFilename = args[0];
        DIMACSInstance instance = Parser.parse(inputFilename);
        adjacency = instance.adjacencyMatrix;
        int size = instance.size;
        
        long startTime = java.lang.System.currentTimeMillis();
        
        // preliminary variables
        boolean isColourable = true;
        int bestSum = Integer.MAX_VALUE;
        Stack<Node> colouring = new Stack<Node>();
        int k = 50;         // This parameter must be tuned
        //System.out.println(maxOrder);
        
        while(isColourable){
            // Initialise domain of size k
            ArrayList<Node> domain = new ArrayList<Node>(size);
            for(int i = 0; i < size; i++){
                ArrayList<Integer> colours = new ArrayList<Integer>(k);
                for(int j = 0; j < k; j++)
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
            
            // Use bestSum somehow so we can iterate and find better colourings rather than stopping once we find one colouring
            isColourable = search(domain, colouring, bestSum, 0); 
            int sum = 0;
            for(Node n : colouring)
                sum += n.colour;
            if(sum <= bestSum)
                bestSum = sum;
            colouring.clear(); // Reset stack
            if(isColourable)
                System.out.println(sum + " cost colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms");
        }
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
        
        
        
        
        
    }
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring, int bestSum,int sum){
        if(domain.isEmpty())
            return sum < bestSum;
        
        if(domain.size() + sum >= bestSum)
            return false;
        
        Node d = pickNode(domain);
                
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
        // chooses vertex with minimum available colours
        // tiebreaking on vertex cardinality(max wins)
        // first vertex found with only one available colour is coloured
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        int maxOrder = 1;
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
}























