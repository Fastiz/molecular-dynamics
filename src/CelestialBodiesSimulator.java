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
    private static final double MARS_VX = 24.991186369972820103;
    private static final double MARS_VY = -0.64123285744192605762;
    private static final double MARS_MASS = 6.4171e23;

    private static final double EARTH_X = -1.4362322641829e8;
    private static final double EARTH_Y = -4.2221842462959e7;
    private static final double EARTH_ANGLE = Math.atan(EARTH_Y / EARTH_X);
    private static final double EARTH_DISTANCE = EARTH_X / Math.cos(EARTH_ANGLE);
    private static final double EARTH_VX = 7.9179041699407207489;
    private static final double EARTH_VY = -28.678710520938155241;
    private static final double EARTH_V_ANGLE = Math.atan(EARTH_VY / EARTH_VX);
    private static final double EARTH_V = EARTH_VX / Math.cos(EARTH_V_ANGLE);
    private static final double EARTH_MASS = 5.97219e24;

    private static final double SUN_MASS = 1.988500e30;

    private static final double SPACESHIP_MASS = 2.0e5;
    private static final double SPACESHIP_DISTANCE = EARTH_DISTANCE + 1500;
    private static final double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
    private static final double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
    private static final double SPACESHIP_V = 8 + 7.12 + EARTH_V;
    private static final double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
    private static final double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

    public CelestialBodiesSimulator() throws IOException {
        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 20, SUN_MASS));
        particlesList.add(new Particle(new Vector(EARTH_X, EARTH_Y), new Vector(EARTH_VX, EARTH_VY), 30, EARTH_MASS));
        particlesList.add(new Particle(new Vector(MARS_X, MARS_Y), new Vector(MARS_VX, MARS_VY), 30, MARS_MASS));

        System.out.println(EARTH_V);
        System.out.println(EARTH_VX + ", " + EARTH_VY);
        System.out.println(SPACESHIP_V);
        System.out.println(SPACESHIP_VX + ", " + SPACESHIP_VY);

        System.out.println(EARTH_DISTANCE);
        System.out.println(EARTH_X + ", " + EARTH_Y);
        System.out.println(SPACESHIP_DISTANCE);
        System.out.println(SPACESHIP_X + ", " + SPACESHIP_Y);
        particlesList.add(new Particle(new Vector(SPACESHIP_X , SPACESHIP_Y), new Vector(SPACESHIP_VX , SPACESHIP_VY), 30, SPACESHIP_MASS));

        TemporalStepAlgorithmInterface algorithm = new Beeman(particlesList, new GravityModel(), 2);

        double maxMagnitude = 0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("dynamic_file"))){
            for(long t=0; t<Integer.MAX_VALUE; t++){
                if(t % 1000 == 0) {
                    bf.write("#T" + t + "\n");
                    List<Particle> current = algorithm.getParticles();
                    for (Particle particle : current) {
                        Vector pos = particle.getPos();

                        double magnitude = pos.magnitude();

                        maxMagnitude = Math.max(magnitude, maxMagnitude);

                        bf.write(pos.getX() + " " + pos.getY() + "\n");
                    }
                }
                algorithm.step();
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
