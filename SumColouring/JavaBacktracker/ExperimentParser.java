import java.util.*;
import java.io.*;
/**
 *  Parse directory containing java backtracker results 
**/

public class ExperimentParser {
    public static void main(String[] args) throws IOException{
        //args[0] contains directory
        //args[1] contains output file
        
        //Open directory
        //Open file in directory
        File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(parseFile(file.toString()));
    }
}
        
        
        //System.out.println(parseFile(args[0]));
    }
    
    static String parseFile(String fname) throws IOException{
        String data;
        String firstSol = "";
        String finalSol = "";
        String completionTime = "";
        try{
            FileReader reader = new FileReader(fname);
            Scanner in = new Scanner(reader);
            String line = in.nextLine();
            
            firstSol = parseLine(line.split(" "));
            String finalSolLine = line;
            
            while(true){
                line = in.nextLine();
                String[] symbols = line.split(" ");
                if(symbols[0].equals("Timed")){
                    completionTime = "null";
                    break;
                }
                if(symbols[0].equals("Completed")){
                    completionTime = symbols[2].substring(0,symbols[2].length()-2);
                    break;
                }
                finalSol = parseLine(symbols);
            }
            
            
        }
        catch(Exception e){
            //System.out.println("exception");
        }
        data = firstSol + " " + finalSol + " " + completionTime;
        return data;
    }
    
    static String parseLine(String[] symbols){
        String quality = symbols[0];
        String time = symbols[5].substring(0,symbols[5].length()-9);
        String nodes = symbols[6];
        
        return quality + " " + time;
    }
    
}