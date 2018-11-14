import java.util.*;
import java.io.*;

public class Parser {
    
    public static DIMACSInstance parse(String inputFilename) throws IOException{    
    
    // Initialise default values for graph
    boolean adjacency[][] = new boolean[1][1];
    int size = -1;
    
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
    
    // Find upper bound colouring for graph
    int maxOrder = 1;
    for(int i = 0; i < size; i++){
        int iOrder = 0;
        for(int j = 0; j < size; j++)
            if(adjacency[i][j])
                iOrder++;
        if(iOrder > maxOrder)
            maxOrder = iOrder;
    }
    
    DIMACSInstance instance = new DIMACSInstance();
    instance.adjacencyMatrix = adjacency;
    instance.size = size;
    
    return instance;
    
    } 
    
}