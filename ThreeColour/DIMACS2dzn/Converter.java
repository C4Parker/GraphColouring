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
        
        //System.out.println(Arrays.deepToString(adjacency));
        
        
        try{
        File target = new File(targetFilename);
        if(target.exists()){
            System.out.println("File with target name already exists, overwrite? (y/n)");
            Scanner userInput = new Scanner(System.in);
            String command = userInput.nextLine().toLowerCase();
            if(command.equals("no") || command.equals("n")){
                System.out.println("Exiting program.");
                return;
            }
            System.out.println("Continuing...");
        } else {
            
        }
        FileWriter writer = new FileWriter(targetFilename);
        writer.write("% Data file generated from " + args[0] + " DIMACS file\n");
        writer.write("vertices = " + size + ";\n\n");
        writer.write("adjacency = [|");
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++)
                writer.write(adjacency[i][j] + ",");
            writer.write("|");
        }
        writer.write("];");
        writer.close();
        }catch(Exception ex){
            System.out.println("No such target");
        }
        System.out.println("Finished");
        // TODO write these in minizinc syntax to target file
        
        
    }
    
    
    
}