import algorithms.Beeman;
import forceCalculators.GravityModel;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CelestialBodiesSimulator {

    private static final double MARS_X = -2.4712389774953e7;
    private static final double MARS_Y = -2.1837372294411e8;
    private static final double MARS_VX = -2.1592385023657e6;
    private static final double MARS_VY = -5.5402518882982e4;
    private static final double MARS_MASS = 6.4171e23;

    private static final double EARTH_X = -1.4362322641829e8;
    private static final double EARTH_Y = -4.2221842462959e7;
    private static final double EARTH_VX = 6.8410692028288e5;
    private static final double EARTH_VY = -2.4778405890091e6;
    private static final double EARTH_MASS = -5.97219e24;

    private static final double SUN_MASS = -1.988500e30;

    public CelestialBodiesSimulator() throws IOException {
        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(100, 0), new Vector(0, 0), 20, 10000000));
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 30, 1000));

        TemporalStepAlgorithmInterface algorithm = new Beeman(particlesList, new GravityModel(), 0.001);

        double maxMagnitude = 0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("dynamic_file"))){
            for(int t=0; t<1000; t++){
                algorithm.step();
                bf.write("#T"+t+"\n");
                List<Particle> current = algorithm.getParticles();
                for(Particle particle : current){
                    Vector pos = particle.getPos();

                    double magnitude = pos.magnitude();

                    maxMagnitude = Math.max(magnitude, maxMagnitude);

                    bf.write(pos.getX() + " " + pos.getY()+"\n");
                }
            }
        }

        try(BufferedWriter bf = new BufferedWriter(new FileWriter("static_file"))){
            bf.write(2 * maxMagnitude + " " + 2 * maxMagnitude + "\n");
            for(Particle particle : particlesList){
                bf.write(particle.getRadius()+"\n");
            }
        }

    }
}
