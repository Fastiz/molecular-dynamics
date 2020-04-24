
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        try{
            CelestialBodiesSimulation celestialBodiesSimulator = new CelestialBodiesSimulation();
            celestialBodiesSimulator.runAnimation();
        }catch (IOException e){
            System.err.println(e);
        }

    }
}
