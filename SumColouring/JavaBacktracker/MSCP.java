import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, attempts to find optimal minimum sum colouring for graph.
*   Search strategies and domain structure currently hardcoded rather than using command line arguments.
*   Command line arguments: <DIMACS file location>
**/
public class MSCP {
    
    // Hacky static declaration
    static boolean adjacency[][];
    static long startTime = java.lang.System.currentTimeMillis();
    static long nodes = 0;
    static FileWriter writer;
    
    public static void main(String args[]) throws IOException{
        
        // Parse input file and initialise graph
        String inputFilename = args[0];
        DIMACSInstance instance = Parser.parse(inputFilename);
        adjacency = instance.adjacencyMatrix;
        int size = instance.size;
        
        String outputFile = args[1];
        writer = new FileWriter(outputFile);
        // search
        try{search();}
        catch(Exception e){}
        
        long endTime = java.lang.System.currentTimeMillis();
        //System.out.println("Completed in " + timeTaken(startTime));
        writer.write("Completed in " + timeTaken(startTime));
        writer.close();
        
    }
    
    public static void search() throws IOException{
        Stack<Node> colouring = new Stack<Node>();
        boolean isColourable = true;
        int targetSum = Integer.MAX_VALUE; // Default value, represents best cost colouring found
        int targetColours = Integer.MAX_VALUE; // represents number of colours in best sum colouring
        
        while(isColourable){
            ArrayList<Node> domain = initialiseDomain(adjacency);
            pruneAdjacent(domain, adjacency);
            
            // Use target as upper bound on new colourings
            isColourable = search(domain, colouring, targetSum, 0, 0, targetColours); 
            int sum = 0;
            int coloursUsed = 0;
            for(Node n : colouring){
                sum += n.colour;
                if(n.colour > coloursUsed)
                    coloursUsed = n.colour;
            }
            if(sum <= targetSum){
                targetSum = sum;
                targetColours = coloursUsed;
            }
            colouring.clear();  // Reset stack
            if(isColourable){
                writer.write(sum + " cost "+ coloursUsed + "-colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms" + "\tNodes: " + nodes);
                writer.write(System.getProperty("line.separator"));
            }
            if(timeOut()){
                writer.write("Timed out.");
                writer.write(System.getProperty("line.separator"));
            }
            nodes = 0;
        }
    }
    
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring, int bestSum, int sum, int coloursUsed, int bestCol){
        nodes++;
        if(domain.isEmpty())
            return sum < bestSum;
        int bestOutcome = sum;
        for(Node n : domain){
            bestOutcome += n.availableColours.get(0);
        }
        if(bestOutcome > bestSum ||  timeOut())
            return false;
        
        Node d = nextMinDeg(domain);
        
        colourD:
        while(!d.availableColours.isEmpty()){
            int colour;
            
            colour = getMCLarge(d, domain);
            
            //colour = getLargest(d);
            
            d.availableColours.remove(Integer.valueOf(colour));
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
            if(search(newDomain, colouring, bestSum, sum+colour, coloursUsed, bestCol)){
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
     *  Picks smallest degree vertex tiebreaking on domain size
    **/
    public static Node nextMinDeg(ArrayList<Node> domain){
        Node d = domain.get(0);
		for(Node n : domain){
			if(n.availableColours.size() == 1)
				return n;
			if(n.order < d.order)
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
    // Value ordering heuristics
    //
    
    public static int getSmallest(Node n){
        return n.availableColours.get(0);
    }
    
    public static int getLargest(Node n){
        return n.availableColours.get(n.availableColours.size()-1);
    }
    
    public static int getRandom(Node n){
        if(n.availableColours.size() == 1){
            return n.availableColours.get(0);
        }
        Random rand = new Random();
        return n.availableColours.get(rand.nextInt(n.availableColours.size()-1));
    }
    
	/**
	 *  Gets colour that conflicts with least number of adjacenct vertices still to be coloured
     *      Favours smaller colour values
	**/
    public static int getLeastConflicts(Node n, ArrayList<Node> domain){
		int[] conflictCount = new int [n.availableColours.size()];
        
		for(Node m : domain){
			if(adjacency[m.vertex][n.vertex]){
				for(Integer colour : m.availableColours){
					if(n.availableColours.contains(colour))
						conflictCount[n.availableColours.indexOf(colour)]++;
				}
			}
		}		
		// Automatic tiebreaking favouring lower colour values due to structure 1..k
		int leastConflictIndex = 0;
		for(int i = 0; i < conflictCount.length; i++){
			if(conflictCount[i] < conflictCount[leastConflictIndex])
				leastConflictIndex = i;
		}
		return n.availableColours.get(leastConflictIndex);
    }
	
	/**
	 *  Gets colour that conflicts with highest number of adjacenct vertices still to be coloured
     *      Favours smaller colour values
	**/
	public static int getMostConflicts(Node n, ArrayList<Node> domain){
		int[] conflictCount = new int [n.availableColours.size()];
        
		for(Node m : domain){
			if(adjacency[m.vertex][n.vertex]){
				for(Integer colour : m.availableColours){
					if(n.availableColours.contains(colour))
						conflictCount[n.availableColours.indexOf(colour)]++;
				}
			}
		}		
		// Automatic tiebreaking favouring lower colour values due to structure 1..k
		int mostConflictIndex = 0;
		for(int i = 0; i < conflictCount.length; i++){
			if(conflictCount[i] > conflictCount[mostConflictIndex])
				mostConflictIndex = i;
		}
		return n.availableColours.get(mostConflictIndex);
    }
    
    /**
	 *  Gets colour that conflicts with least number of adjacenct vertices still to be coloured
     *      Favours larger colour values
	**/
    public static int getLCLarge(Node n, ArrayList<Node> domain){
		int[] conflictCount = new int [n.availableColours.size()];
        
		for(Node m : domain){
			if(adjacency[m.vertex][n.vertex]){
				for(Integer colour : m.availableColours){
					if(n.availableColours.contains(colour))
						conflictCount[n.availableColours.indexOf(colour)]++;
				}
			}
		}		
		// Automatic tiebreaking favouring lower colour values due to structure 1..k
		int leastConflictIndex = 0;
		for(int i = 0; i < conflictCount.length; i++){
			if(conflictCount[i] <= conflictCount[leastConflictIndex])
				leastConflictIndex = i;
		}
		return n.availableColours.get(leastConflictIndex);
    }
	
	/**
	 *  Gets colour that conflicts with highest number of adjacenct vertices still to be coloured
     *      Favours larger colour values
	**/
	public static int getMCLarge(Node n, ArrayList<Node> domain){
		int[] conflictCount = new int [n.availableColours.size()];
        
		for(Node m : domain){
			if(adjacency[m.vertex][n.vertex]){
				for(Integer colour : m.availableColours){
					if(n.availableColours.contains(colour))
						conflictCount[n.availableColours.indexOf(colour)]++;
				}
			}
		}		
		// Automatic tiebreaking favouring lower colour values due to structure 1..k
		int mostConflictIndex = 0;
		for(int i = 0; i < conflictCount.length; i++){
			if(conflictCount[i] >= conflictCount[mostConflictIndex])
				mostConflictIndex = i;
		}
		return n.availableColours.get(mostConflictIndex);
    }
    
    
    //
    // Domain initialisation
    //
    
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
    
    /**
     *  Removes values from domain that cannot appear in optimal solution such that domain becomes
     *      1..min(m+1) where m is maximum degree of vertex and all adjacenct vertices
    **/
    public static void pruneAdjacent(ArrayList<Node> domain, boolean[][] adjacency){
        for(Node n : domain){
            ArrayList<Integer> newDomain = new ArrayList<Integer>();
            int maxAdjacentDegree = 0;
            for(int i = 0; i < adjacency.length; i++){
                if(adjacency[n.vertex][i] && domain.get(i).order > maxAdjacentDegree)
                    maxAdjacentDegree = domain.get(i).order;
            }
            if(maxAdjacentDegree < n.availableColours.size()){
                for(int i = 1; i <= maxAdjacentDegree + 1; i++)
                    newDomain.add(i);
                n.availableColours = newDomain;
            }
        }
    }
    
    public static boolean timeOut(){
        return java.lang.System.currentTimeMillis() - startTime > 1000000;
    }
    
    public static String timeTaken(long startTime){
        return (java.lang.System.currentTimeMillis() - startTime) + "ms";
    }
    
}