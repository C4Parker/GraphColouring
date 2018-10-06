import java.io.*;
import java.util.*;
/**
*   Takes DIMACS file as command line argument, determines whether
*   the graph can be coloured with 3 colours.
**/
public class Backtracker {
    
    public static void main(String args[]) throws IOException{
        
        String inputFilename = args[0];
        
        // Initialise default values for graph
        int size = -1;
        boolean adjacency[][] = new boolean[1][1];
        
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
        System.out.println(search(domain, adjacency));
        
    }
    
    public static boolean search(ArrayList<Node> domain, boolean[][] adjacencyMatrix){
        if(domain.size() == 0)
            return true;
        //choose some node in domain
        Node d = domain.get(0);
        
        for(String colour : d.availableColours){
            ArrayList<Node> newDomain = new ArrayList<Node>(domain.size());
            for(Node n : domain){
                if(n.vertex != d.vertex)
                    newDomain.add(n);
            }
            for(Node n : domain){
                if(adjacencyMatrix[n.vertex][d.vertex]){
                    n.availableColours.remove(colour);
                    if(n.availableColours.isEmpty())
                        break;
                }
            }
            return search(newDomain, adjacencyMatrix);
        }
        return false;
    }
}

class Node{
	public int vertex;
	public ArrayList<String> availableColours;
    public String colour;
}























