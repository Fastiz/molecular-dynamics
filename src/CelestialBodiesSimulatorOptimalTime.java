import algorithms.Beeman;
import forceCalculators.GravityModel;
import interfaces.TemporalStepAlgorithmInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Particle;
import models.Vector;

public class CelestialBodiesSimulatorOptimalTime {

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
    private static final double EARTH_V_ANGLE = Math.atan(EARTH_VY / EARTH_VX);
    private static final double EARTH_V = EARTH_VX / Math.cos(EARTH_V_ANGLE);
    private static final double EARTH_MASS = 5.97219e24;

    private static final double SUN_MASS = 1.988500e30;

    private static final double SPACESHIP_MASS = 2.0e5;
    private static final double SPACESHIP_DISTANCE = EARTH_DISTANCE + 1500;
    private static final double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
    private static final double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
    private static final double SPACESHIP_V = 8 + 7.12;
    private static final double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
    private static final double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

    public CelestialBodiesSimulatorOptimalTime() throws IOException {
        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 20, SUN_MASS));
        particlesList.add(new Particle(new Vector(EARTH_X, EARTH_Y), new Vector(EARTH_VX, EARTH_VY), 30, EARTH_MASS));
        particlesList.add(new Particle(new Vector(MARS_X, MARS_Y), new Vector(MARS_VX, MARS_VY), 30, MARS_MASS));


        (new File("results/gravity/")).mkdir();

        Utils.deleteFiles("results/gravity/");

        int TIME_STEP = 1000;
        int TIME_ITERATIONS = 100;

        for(int it=1; it<TIME_ITERATIONS+1; it++){
            System.out.println("Simulating launch with offset: " + TIME_STEP*it +" s and index: " + it +".");

            try(BufferedWriter bf = new BufferedWriter(new FileWriter("results/gravity/sim"+it))){
                Beeman algorithm = new Beeman(new ArrayList<>(particlesList), new GravityModel(), 100);

                bf.write((it*TIME_STEP/1000) + "\n");
                for(long t=0; t<1000000; t++){
                    if(t == it*TIME_STEP){
                        Particle earth = algorithm.getParticles().get(1);

                        double EARTH_V_ANGLE = Math.atan(earth.getVel().getY() / earth.getVel().getX());
                        double EARTH_ANGLE = Math.atan2(earth.getPos().getY(), earth.getPos().getX());
                        double SPACESHIP_DISTANCE = earth.getPos().magnitude() + 1500;
                        double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
                        double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
                        double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
                        double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

                        algorithm.addParticle(new Particle(new Vector(SPACESHIP_X , SPACESHIP_Y), new Vector(SPACESHIP_VX, SPACESHIP_VY), 30, SPACESHIP_MASS));
                    }

                    if(t % 1000 == 0) {
                        bf.write("#T" + t + "\n");
                        List<Particle> current = algorithm.getParticles();
                        for (Particle particle : current) {
                            Vector pos = particle.getPos();

                            bf.write(pos.getX() + " " + pos.getY() + "\n");
                        }

                    }

                    algorithm.step();
                }
            }
        }

    }
}
