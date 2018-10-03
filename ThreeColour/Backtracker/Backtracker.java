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
        
        ArrayList<String> colours = new ArrayList<String>();
        colours.add("red");
        colours.add("green");
        colours.add("blue");
        ArrayList<ArrayList<String>> vertices = new ArrayList<ArrayList<String>>(size);
        for (int i = 0; i < size; i++)
            vertices.add(colours);
        
        
        
        for (int i = 0; i < size; i++)
            System.out.println(vertices.get(i));
        
        
    }
    
    public boolean search(ArrayList<ArrayList<String>> domain, boolean[][] adjacencyMatrix){
        if(domain.isEmpty())
            return true;
        for(ArrayList<String> d : domain){
            int vertexNumber = domain.indexOf(d);
            for(int i = 0; i < domain.size(); i++){
                if(adjacencyMatrix[vertexNumber][i]){
                    // remove colour
                    if(domain.get(i).isEmpty())
                        continue;
                }
            }
            ArrayList<ArrayList<String>> newDomain = new ArrayList<ArrayList<String>>(domain.size());
            
        }
        return false;
    }
}
























