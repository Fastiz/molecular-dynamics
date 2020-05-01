
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        try{
            SocialSimulation socialSimulation = new SocialSimulation();
        }catch (IOException e){
            System.err.println(e);
        }
    }
}
