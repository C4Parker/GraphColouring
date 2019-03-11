import java.io.*;
import java.util.*;

/**
 *   
**/
public class GC {
    // Initialise default values for graph
    
    public static int findChromatic(int[][] adjacency){
        
        int chi = 0;
        
        int maxOrder = 1;
        for(int i = 0; i < adjacency.length; i++){
            int iOrder = 0;
            for(int j = 0; j < adjacency.length; j++)
                if(adjacency[i][j]==1)
                    iOrder++;
            if(iOrder > maxOrder)
                maxOrder = iOrder;
        }
        
        boolean isColourable = true;
        
        long startTime = java.lang.System.currentTimeMillis();
        int currentColouring = maxOrder;
        while(isColourable){
            
            // Builds new domain of size k
            ArrayList<Node> domain = new ArrayList<Node>(adjacency.length);
            for(int i = 0; i < adjacency.length; i++){
                ArrayList<Integer> colours = new ArrayList<Integer>(currentColouring);
                for(int j = 0; j < currentColouring; j++)
                    colours.add(j+1);
                Node n = new Node();
                n.vertex = i;
                n.availableColours = colours;
                int nOrder = 0;
                for(int j = 0; j < adjacency.length; j++)
                    if(adjacency[i][j]==1)
                        nOrder++;
                n.order = nOrder;
                domain.add(n);
            }
            
            isColourable = search(domain, 0, adjacency);
            if(isColourable)
                chi = currentColouring;
            currentColouring--;
        }
        return chi;
    }

    
    public static boolean search(ArrayList<Node> domain, int coloursUsed, int[][] adjacency){
        
        if(domain.isEmpty())
            return true;
        Node d = domain.get(0);
        int dVertex = d.vertex;
        int dColours = d.availableColours.size();
        int maxOrder = 1;
        for(Node n : domain){
            int nColours = n.availableColours.size();
            if (nColours <= dColours){
                if(nColours == dColours && n.order > maxOrder)
                    maxOrder = n.order;
                d = n;
                dVertex = n.vertex;
                dColours = n.availableColours.size();
            }
            if(nColours == 1)
                break;
        }
        // Not sure what's wrong here but symmetry isn't broken here properly so have reverted to the longer code below
/*       colourD:
        for(Integer colour : d.availableColours){
            if(colour <= coloursUsed + 1){
                if(colour == coloursUsed + 1)
                    coloursUsed++;
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]==1){
                        n.availableColours.remove(colour);
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                if(search(newDomain, coloursUsed, adjacency))
                    return true;
            }
        }
        return false;*/
               colourD:
        for(Integer colour : d.availableColours){
            if(colour <= coloursUsed) {
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]==1){
                        n.availableColours.remove(colour);
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                if(search(newDomain, coloursUsed, adjacency)){
                    return true;
                }
            }
            if(colour == coloursUsed + 1) {
                ArrayList<Node> newDomain = new ArrayList<Node>();
                for(Node n : domain){
                    if(n.vertex != d.vertex)
                        newDomain.add(n.clone());
                }
                for(Node n : newDomain){
                    if(adjacency[n.vertex][d.vertex]==1){
                        n.availableColours.remove(colour);
                        if(n.availableColours.isEmpty())
                            continue colourD;
                    }
                }
                if(search(newDomain, coloursUsed + 1, adjacency)){
                    return true;
                }
            }
        }
        return false;
        
    }
}

class Node{
	public int vertex;
    public int order;
	public ArrayList<Integer> availableColours;
    
    @Override
    public Node clone(){
        Node clone = new Node();
        clone.vertex = this.vertex;
        clone.order = this.order;
        clone.availableColours = new ArrayList<Integer>();
        for(Integer c : this.availableColours)
            clone.availableColours.add(c);
        
        return clone;
    }
    
}























