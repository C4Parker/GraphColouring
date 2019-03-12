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
        //Parse
            //first line is first solution
            //second last line is last solution OR
            //third last is last solution second last is last solution
            //last line is timeout or completion timeout
        
        System.out.println(parseFile(args[0]));
    }
    
    static String parseFile(String fname) throws IOException{
        String data;
        String firstSol;
        String finalSol;
        boolean timeOut = false;
        String completionTime = "";
        try{
            FileReader reader = new FileReader(fname);
            Scanner in = new Scanner(reader);
            String line = in.nextLine();
            
            firstSol = parseLine(line.split(" "));
            
            String finalSolLine = line;
            
            while(!timeOut){
                line = in.nextLine();
                String[] symbols = line.split(" ");
                if(symbols[0].equals("Timed")){
                    timeOut = true;
                    completionTime = "null";
                }
                if(symbols[0].equals("Completed")){
                    completionTime = symbols[2].substring(0,symbols[2].length()-2);
                    break;
                }
                finalSolLine = line;
            }
            
            finalSol = parseLine(finalSolLine.split(" "));
            
        }
        catch(Exception e){
            firstSol = "";
            finalSol = "";
            System.out.println("exception");
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