import java.util.ArrayList;

class Node{
	public int vertex;
    public int order;
	public ArrayList<Integer> availableColours;
    public Integer colour;
    
    public Node(int vertex, int order, Integer colour, ArrayList<Integer> availableColours){
        this.vertex = vertex;
        this.order = order;
        this.availableColours = availableColours;
        this.colour = colour;
    }
    
    public Node(int vertex, Integer colour){
        this.vertex = vertex;
        this.colour = colour;
    }
    
    public Node(){
    }
    
    @Override
    public Node clone(){
        Node clone = new Node();
        clone.vertex = this.vertex;
        clone.order = this.order;
        clone.availableColours = new ArrayList<Integer>();
        for(int c : this.availableColours)
            clone.availableColours.add(c);
        
        return clone;
    }
    
    @Override
    public String toString(){
        return "{" + vertex + ": " + availableColours.toString() + "}";
    }
    
}
