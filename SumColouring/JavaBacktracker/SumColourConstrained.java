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
    static int nodes = 0;
    
    public static void main(String args[]) throws IOException{
        
        // Parse input file and initialise graph
        String inputFilename = args[0];
        DIMACSInstance instance = Parser.parse(inputFilename);
        adjacency = instance.adjacencyMatrix;
        int size = instance.size;
        
        // preliminary variables
        int k = 8;         // Maximum number of colours to colour graph with
        
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
            ArrayList<Node> domain = initialiseDomain(adjacency, numColours);
            
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
                System.out.println(sum + " cost "+ coloursUsed + "-colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms" + "\tNodes: " + nodes);
            nodes = 0;
        }
    }
    
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring, int bestSum, int sum, int coloursUsed){
        nodes++;
        if(domain.isEmpty())
            return sum < bestSum;
        int bestOutcome = sum;
        for(Node n : domain){
            bestOutcome += n.availableColours.get(0);
        }
        if(bestOutcome >= bestSum)
            return false;
        
        Node d = nextDeg(domain);
        
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
                if(colour == coloursUsed + 1)
                    coloursUsed++;
                if(search(newDomain, colouring, bestSum, sum+colour, coloursUsed)){
                    d.colour = colour;
                    colouring.push(d);
                    return true;
                }
        }
        return false;
    }
    
    //
    // Search ordering heuristics
    //
    
    /**
	 *  Picks next vertex arbitrarily
	**/
	public static Node next(ArrayList<Node> domain){
        return domain.get(0);
    }
    
    /**
	 *  Picks next vertex according to DSATUR heuristic, i.e on domain size tiebreaking on degree
	**/
	public static Node nextDSATUR(ArrayList<Node> domain){
        Node d = domain.get(0);
		int dSize = d.availableColours.size();
		for(Node n : domain){
			int nSize = n.availableColours.size();
			if(nSize == 1)
				return n;
			if(nSize < dSize){
				d = n;
				dSize = nSize;
			}
			else if(nSize == dSize && n.order > d.order){
				d = n;
				dSize = nSize;
			}
		}
        return d;
    }
    
    /**
     *  Picks largest degree vertex tiebreaking on domain size
    **/
    public static Node nextDeg(ArrayList<Node> domain){
        Node d = domain.get(0);
		for(Node n : domain){
			if(n.availableColours.size() == 1)
				return n;
			if(n.order > d.order)
				d = n;
			else if(n.order == d.order && n.availableColours.size() < d.availableColours.size())
				d = n;
		}
		return d;
	}
    
    /**
     *  Picks cheapest available cost vertex tiebreaking on domain size and vertex order
    **/
    public static Node nextCheapest(ArrayList<Node> domain){
        Node d = domain.get(0);
        int dOrder = d.order;
        int dColours = d.availableColours.size();
        int dCost = d.availableColours.get(0);
        if(dColours == 1)
            return d;
        
        for(Node n : domain){
            int nOrder = n.order;
            int nColours = n.availableColours.size();
            int nCost = n.availableColours.get(0);
            if(nColours == 1)
                return n;
            
            if(nCost < dCost){
                d = n;
                dOrder = nOrder;
                dColours = nColours;
                dCost = nCost;
            }
            
            // Tiebreaking on domain size and order
            else if(nCost == dCost){
                if(nColours < dColours || nOrder < dOrder){
                    d = n;
                    dOrder = nOrder;
                    dColours = nColours;
                    dCost = nCost;
                }
            }
        }
        
        return d;
    }

    
    
    
    //
    // Domain initialisation
    //
    
    /**
     * Initialise domain of size, with d+1 colours for vertex of degree d
    **/
    public static ArrayList<Node> initialiseDomain(boolean[][] adjacency, int k){
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
            
            ArrayList<Integer> colours = new ArrayList<Integer>(Math.min(nOrder + 1, k));
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