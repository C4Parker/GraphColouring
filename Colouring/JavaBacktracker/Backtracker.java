import java.io.*;
import java.util.*;

/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with k colours.
*   Command line arguments: <DIMACS file location> <k-colours>
**/
public class Backtracker {
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
        
        int maxOrder = 1;
        for(int i = 0; i < size; i++){
            int iOrder = 0;
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    iOrder++;
            if(iOrder > maxOrder)
                maxOrder = iOrder;
        }
        
        
        
        Stack<String> colouring = new Stack<String>();
        boolean isColourable = true;
        
        long startTime = java.lang.System.currentTimeMillis();
        int currentColouring = maxOrder;
        while(isColourable){
            
            // Builds new domain of size k
            ArrayList<Node> domain = new ArrayList<Node>(size);
            for(int i = 0; i < size; i++){
                ArrayList<String> colours = new ArrayList<String>(currentColouring);
                for(int j = 0; j < currentColouring; j++)
                    colours.add(Integer.toString(j+1));
                Node n = new Node();
                n.vertex = i;
                n.availableColours = colours;
                int nOrder = 0;
                for(int j = 0; j < size; j++)
                    if(adjacency[i][j])
                        nOrder++;
                n.order = nOrder;
                n.uncolouredOrder = nOrder;
                //n.colour = null;
                domain.add(n);
            }
            
            isColourable = search(domain, colouring, 0);
            if(isColourable)
                System.out.println(currentColouring + " colouring found in " + (java.lang.System.currentTimeMillis()-startTime) + "ms");
            currentColouring--;
        }
        //System.out.println(search(domain, colouring));
        
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
        
        
    }
    
    public static boolean search(ArrayList<Node> domain, Stack<String> colouring, int coloursUsed){
        if(domain.isEmpty())
            return true;

        // chooses vertex with minimum available colours
        // no tiebreaking
        // first vertex found with only one available colour is coloured
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        for(Node n : domain){
            int nColours = n.availableColours.size();
            if (nColours < dColours){
                d = n;
                dVertex = n.vertex;
                dColours = n.availableColours.size();
            }
            if(nColours == 1)
                break;
        }
        
       colourD:
        for(String colour : d.availableColours){
            if(Integer.parseInt(colour) <= coloursUsed) {
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]){
                        n.availableColours.remove(colour);
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                if(search(newDomain, colouring, coloursUsed)){
                    String s = "(" + d.vertex + " : " + colour + ")";
                    colouring.push(s);
                    return true;
                }
            }
            if(Integer.parseInt(colour) == coloursUsed + 1) {
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]){
                        n.availableColours.remove(colour);
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                if(search(newDomain, colouring, coloursUsed + 1)){
                    String s = "(" + d.vertex + " : " + colour + ")";
                    colouring.push(s);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean searchDSATUR(ArrayList<Node> domain, Stack<String> colouring){
        if(domain.isEmpty())
            return true;
        
        // chooses vertex with minimum available colours
        // tiebreaking on vertex cardinality
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
        
       colourD:
        for(String colour : d.availableColours){
            ArrayList<Node> newDomain = new ArrayList<Node>();
            for(Node n : domain){
                if(n.vertex != d.vertex)
                    newDomain.add(n.clone());
            }
            for(Node n : newDomain){
                if(adjacency[n.vertex][d.vertex]){
                    n.availableColours.remove(colour);
                    if(n.availableColours.isEmpty())
                        continue colourD;
                }
            }
            if(searchDSATUR(newDomain, colouring)){
                String s = "(" + d.vertex + " : " + colour + ")";
                colouring.push(s);
                return true;
            }
        }
        return false;
    }
    
    public static boolean searchDSATUROptimised(ArrayList<Node> domain, Stack<String> colouring){
        if(domain.isEmpty())
            return true;
                
        // chooses vertex with minimum available colours
        // tiebreaking on vertex uncoloured/affected cardinality
        // first vertex found with only one available colour is coloured
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        int maxOrder = 1;
        for(Node n : domain){
            int nColours = n.availableColours.size();
            if (nColours <= dColours){
                if(nColours == dColours && n.uncolouredOrder > maxOrder)
                    maxOrder = n.uncolouredOrder;
                d = n;
                dVertex = n.vertex;
                dColours = n.availableColours.size();
            }
            if(nColours == 1)
                break;
        }
        
       colourD:
        for(String colour : d.availableColours){
            ArrayList<Node> newDomain = new ArrayList<Node>();
            for(Node n : domain){
                if(n.vertex != d.vertex)
                    newDomain.add(n.clone());
            }
            for(Node n : newDomain){
                if(adjacency[n.vertex][d.vertex]){
                    n.availableColours.remove(colour);
                    if(n.availableColours.isEmpty())
                        continue colourD;
                    n.uncolouredOrder--;
                }
            }
            if(searchDSATUROptimised(newDomain, colouring)){
                String s = "(" + d.vertex + " : " + colour + ")";
                colouring.push(s);
                return true;
            }
        }
        return false;
    }
    
}

class Node{
	public int vertex;
    public int order;
    public int uncolouredOrder;
	public ArrayList<String> availableColours;
    //public String colour;
    
    @Override
    public Node clone(){
        Node clone = new Node();
        clone.vertex = this.vertex;
        clone.order = this.order;
        clone.uncolouredOrder = this.uncolouredOrder;
        clone.availableColours = new ArrayList<String>();
        for(String c : this.availableColours)
            clone.availableColours.add(c);
        
        return clone;
    }
    
}























