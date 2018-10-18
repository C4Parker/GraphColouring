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
        
        Stack<Node> colouring = new Stack<Node>();
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
                n.colour = null;
                domain.add(n);
            }
            
            chromaticNumerFound = search(domain, colouring);
            if(chromaticNumerFound)
                System.out.println("Chromatic number found: " + k);
        }
        //System.out.println(search(domain, colouring));
        
        
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
        
        
        int sum = 0;
        for(Node n : colouring)
            sum += Integer.parseInt(n.colour);
        System.out.println("Sum: "+sum);
        
        
    }
    
    public static boolean search(ArrayList<Node> domain, Stack<Node> colouring){
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
                d.colour = colour;
                //String s = "(" + d.vertex + " : " + colour + ")";
                colouring.push(d);
                return true;
            }
        }
        return false;
    }
}

class Node{
	public int vertex;
	public ArrayList<String> availableColours;
    public String colour;
    
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























