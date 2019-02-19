public class SampleGenerator {
    
    public static void main(String args[]){
        for(int size = 10; size <= 40; size += 2)
            for(double density = 0.05; density < 1; density += 0.05)
                for(int i = 1; i <= 3; i++){
                    String fname = "instances/"+size+"_"+String.format("%.2f", density)+"_"+i+".col";
                    String fargs[] = {fname, Integer.toString(size), Double.toString(density)};
                    Generator.main(fargs);
                }
    }    
}