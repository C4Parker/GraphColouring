import java.io.*;
import java.util.*;
/**
*   Reads in DIMACS graph instance file and converts into
*   MiniZinc data file with its size and adjacency matrix.
**/


public class DIMACS2dzn {
        
    public static void main(String args[]) throws IOException{
        String inputFilename = args[0];
        String targetFilename =  args[1];
        
        // Initialise default values for graph
        int size = -1;
        int adjacency[][] = new int[1][1];
        
        // Read in data from file
        try{
        FileReader reader = new FileReader(inputFilename);
        Scanner in = new Scanner(reader);
        String line = in.nextLine();
        
            while(line != null){
                String symbols[] = line.split(" ");
                if(symbols[0].equals("p")){
                    size = Integer.parseInt(symbols[2]);
                    adjacency = new int[size][size];
                }else if(symbols[0].equals("e")){
                    int v1 = Integer.parseInt(symbols[1]);
                    int v2 = Integer.parseInt(symbols[2]);
                    adjacency[v1-1][v2-1] = 1;
                }
                line = in.nextLine();
            }
            
        in.close();
        }catch(Exception ex){}
        
        
        
        // Remove edges connecting vertex to itself
        for(int i = 0; i < size; i++)
            adjacency[i][i] = 0;
        
        // Make adjacency matrix symmetric
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if(adjacency[i][j] == 1)
                    adjacency[j][i] = 1;
        
        
        
        try{
        File target = new File(targetFilename);
        if(target.exists()){
            Scanner userInput = new Scanner(System.in);
            String command = "";
            while(!command.equals("y") && !command.equals("yes")){
            System.out.println("File with target name already exists, overwrite? (y/n)");
            command = userInput.nextLine().toLowerCase();
            if(command.equals("no") || command.equals("n")){
                System.out.println("Exiting program.");
                return;
            }
            }
        } else {
            
        }
        FileWriter writer = new FileWriter(targetFilename);
        writer.write("% Data file generated from " + args[0] + " DIMACS file\n");
        writer.write("size = " + size + ";\n\n");
        writer.write("adjacency = [|");
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++)
                writer.write(adjacency[i][j] + ",");
            writer.write("\n|");
        }
        writer.write("];");
        writer.close();
        }catch(Exception ex){
            System.out.println("No such target");
        }
        System.out.println("Finished");
        
        
    }
    
    
    
}