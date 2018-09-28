import java.io.*;
import java.util.*;

public class Converter {
        
    public static void main(String args[]) throws IOException{
        String inputFilename = args[0];
        String targetFilename =  args[1];
        
        // Initialise default values for graph
        int size = -1;
        boolean adjacency[][] = new boolean[1][1];
        
        // Read in data from file
        FileReader reader = new FileReader(inputFilename);
        Scanner in = new Scanner(reader);
        String line = in.nextLine();
        try{
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
        }catch(Exception ex){}
        
        in.close();
        
        // Remove edges connecting vertex to itself
        for(int i = 0; i < size; i++)
            adjacency[i][i] = false;
        
        // Make adjacency matrix symmetric
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if(adjacency[i][j])
                    adjacency[j][i] = true;
        
        System.out.println(Arrays.deepToString(adjacency));
        
        // TODO write these in minizinc syntax to target file
        
        
    }
    
}