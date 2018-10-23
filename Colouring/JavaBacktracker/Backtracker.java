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
        
        /*int k = 3; // number of colours
        try{
            k = Integer.parseInt(args[1]);
        } catch(Exception e) {
            System.out.println("k-colours not specified, testing graph for 3 colouring.");
        }*/
        
        
        
        Stack<String> colouring = new Stack<String>();
        boolean chromaticNumerFound = false;
        
        long startTime = java.lang.System.currentTimeMillis();
        
        
        for(int k = 1; k < size && !chromaticNumerFound; k++){
            ArrayList<Node> domain = new ArrayList<Node>(size);
            for(int i = 0; i < size; i++){
                ArrayList<String> colours = new ArrayList<String>(k);
                for(int j = 0; j < k; j++)
                    colours.add(Integer.toString(j+1));
                Node n = new Node();
                n.vertex = i;
                n.availableColours = colours;
                int nOrder = 0;
                for(int j = 0; j < size; j++)
                    if(adjacency[i][j])
                        nOrder++;
                n.order = nOrder;
                //n.colour = null;
                domain.add(n);
            }
            
            chromaticNumerFound = searchDSATUR(domain, colouring);
            if(chromaticNumerFound)
                System.out.println("Chromatic number found: " + k);
        }
        //System.out.println(search(domain, colouring));
        
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
        
        
    }
    
    public static boolean search(ArrayList<Node> domain, Stack<String> colouring){
        if(domain.isEmpty())
            return true;
        
        //choose some node in domain
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
            if(search(newDomain, colouring)){
                String s = "(" + d.vertex + " : " + colour + ")";
                colouring.push(s);
                return true;
            }
        }
        return false;
    }
    
    public static boolean searchDSATUR(ArrayList<Node> domain, Stack<String> colouring){
        if(domain.isEmpty())
            return true;
        
        //choose some initial node in domain
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        int maxOrder = 1;
        for(Node n : domain){
            int nColours = n.availableColours.size();
            if (nColours < dColours){
                d = n;
                dVertex = n.vertex;
                dColours = n.availableColours.size();
            }
            if (nColours == dColours)
                if(n.order > maxOrder){
                    maxOrder = n.order;
                    d = n;
                    dVertex = n.vertex;
                    dColours = n.availableColours.size();
                }
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
            if(search(newDomain, colouring)){
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
	public ArrayList<String> availableColours;
    //public String colour;
    
    @Override
    public Node clone(){
        Node clone = new Node();
        clone.vertex = this.vertex;
        clone.availableColours = new ArrayList<String>();
        for(String c : this.availableColours)
            clone.availableColours.add(c);
        
        return clone;
    }
    
}























