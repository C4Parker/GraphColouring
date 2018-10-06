import java.io.*;
import java.util.*;

/**
*       Generates random graph
*       Takes arguments <output file> <graph size(0,+inf)> <graph density(0,1)>
**/

public class Generator{
    public static void main(String[] args){
        
        String targetFilename = args[0];
        int size = Integer.parseInt(args[1]);
        double probability = Double.parseDouble(args[2]);
        boolean adjacencyMatrix[][] = new boolean[size][size];
        
        int edges = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < i; j++){
                adjacencyMatrix[i][j] = (Math.random() < probability);
                edges++;
            }
        }
        
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
        writer.write("c File: "+targetFilename+"\nc\nc Description: Random graph of size "+size+" vertices and P("+probability+") of edges between any two vertices.\n");
        writer.write("p col "+size+" "+edges+"\n");
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++)
                if(adjacencyMatrix[i][j])
                    writer.write("e "+i+" "+j+"\n");
        }
        writer.close();
        }catch(Exception ex){
        }
        
    }
}