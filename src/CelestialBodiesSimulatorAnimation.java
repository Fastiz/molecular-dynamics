import algorithms.Beeman;
import algorithms.LeapFrog;
import algorithms.SimpleBeeman;
import com.sun.org.apache.xerces.internal.xs.XSTerm;
import forceCalculators.GravityModel;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class CelestialBodiesSimulatorAnimation {

    private static final double MARS_X = -2.4712389774953e7;
    private static final double MARS_Y = -2.1837372294411e8;
    private static final double MARS_VX = 24.991186369972820103;
    private static final double MARS_VY = -0.64123285744192605762;
    private static final double MARS_MASS = 6.4171e23;

    private static final double EARTH_X = -1.4362322641829e8;
    private static final double EARTH_Y = -4.2221842462959e7;
    private static final double EARTH_ANGLE = Math.atan2(EARTH_Y, EARTH_X);
    private static final double EARTH_DISTANCE = Math.abs(EARTH_X / Math.cos(EARTH_ANGLE));
    private static final double EARTH_VX = 7.9179041699407207489;
    private static final double EARTH_VY = -28.678710520938155241;
    private static final double EARTH_V_ANGLE = Math.atan2(EARTH_VY,  EARTH_VX);
    private static final double EARTH_V = EARTH_VX / Math.cos(EARTH_V_ANGLE);
    private static final double EARTH_MASS = 5.97219e24;

    private static final double SUN_MASS = 1.988500e30;

    private static final double SPACESHIP_MASS = 2.0e5;
    private static final double SPACESHIP_DISTANCE = EARTH_DISTANCE + 1500 + 6371.01;
    private static final double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
    private static final double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
    private static final double SPACESHIP_V = 8 + 7.12;
    private static final double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
    private static final double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

    public CelestialBodiesSimulatorAnimation() throws IOException {
        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 20, SUN_MASS));
        particlesList.add(new Particle(new Vector(EARTH_X, EARTH_Y), new Vector(EARTH_VX, EARTH_VY), 30, EARTH_MASS));
        particlesList.add(new Particle(new Vector(MARS_X, MARS_Y), new Vector(MARS_VX, MARS_VY), 30, MARS_MASS));

        int step = 50;
        SimpleBeeman algorithm = new SimpleBeeman(particlesList, new GravityModel(), step);


        double maxMagnitude = 0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("dynamic_file"))){
            boolean flag = false;
            for(long t=0; t< 400000; t++){
                if (!flag && t >= 131 * 24 * 60 * 60 / step) {
                    flag = true;
                    Particle earth = algorithm.getParticles().get(1);

                    double EARTH_V_ANGLE = Math.atan2(earth.getVel().getY(), earth.getVel().getX());
                    double EARTH_ANGLE = Math.atan2(earth.getPos().getY(), earth.getPos().getX());
                    double SPACESHIP_DISTANCE = earth.getPos().magnitude() + 1500 + 6371.01;
                    double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
                    double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
                    double SPACESHIP_VX = (SPACESHIP_V * Math.cos(EARTH_V_ANGLE)) + (-Math.sin(EARTH_ANGLE) * earth.getVel().magnitude());
                    double SPACESHIP_VY = (SPACESHIP_V * Math.sin(EARTH_V_ANGLE)) + (Math.cos(EARTH_ANGLE) * earth.getVel().magnitude());

                    algorithm.addParticle(new Particle(new Vector(SPACESHIP_X, SPACESHIP_Y), new Vector(SPACESHIP_VX, SPACESHIP_VY), 30, SPACESHIP_MASS));
                }
                if(t % 1200 == 0) {
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
