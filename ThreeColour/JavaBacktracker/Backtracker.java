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
        
        int k = 3; // number of colours
        try{
            k = Integer.parseInt(args[1]);
        } catch(Exception e) {
            System.out.println("k-colours not specified, testing graph for default 3 colouring.");
        }
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
        
        long startTime = java.lang.System.currentTimeMillis();
        System.out.println(search(domain));
        long endTime = java.lang.System.currentTimeMillis();
        System.out.println("Completed in " + Long.toString(endTime-startTime) + "ms");
        
    }
    
    public static boolean search(ArrayList<Node> domain){
        if(domain.isEmpty())
            return true;
        
        //choose some node in domain
        Node d = domain.get(0);
        for(String colour : d.availableColours){
            ArrayList<Node> newDomain = new ArrayList<Node>(domain.size()-1);
            for(Node n : domain){
                if(n.vertex != d.vertex)
                    newDomain.add(n);
            }
            for(Node n : newDomain){
                if(adjacency[n.vertex][d.vertex]){
                    n.availableColours.remove(colour);
                    if(n.availableColours.isEmpty())
                        break;
                }
            }
            return search(newDomain);
        }
        return false;
    }
}

class Node{
	public int vertex;
	public ArrayList<String> availableColours;
    public String colour;
}























