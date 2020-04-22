import algorithms.Beeman;
import algorithms.SimpleBeeman;
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

public class DistanceTest {

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

    public DistanceTest() throws IOException {

        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 20, SUN_MASS));
        particlesList.add(new Particle(new Vector(EARTH_X, EARTH_Y), new Vector(EARTH_VX, EARTH_VY), 30, EARTH_MASS));
        particlesList.add(new Particle(new Vector(MARS_X, MARS_Y), new Vector(MARS_VX, MARS_VY), 30, MARS_MASS));

        (new File("results")).mkdir();
        (new File("results/velocity/")).mkdir();

        Utils.deleteFiles("results/velocity/");

        int TIME_STEP = 1000;
        int TIME_ITERATIONS = 100000;
        int step = 73;
        double year = 3600 * 24 * 140 / (73.0);

        int minSecond = 0;
        double minGlobalDistance = Double.MAX_VALUE;
        for (int second = 203100* 60; second < 203100 * 60 + 1; second += step) {

            System.out.println("Day: " + second / (24 * 60 * 60) + " Second: " + second);
            SimpleBeeman algorithm = new SimpleBeeman(new ArrayList<>(particlesList), new GravityModel(), step);

            Vector minDistance = new Vector(Double.MAX_VALUE, Double.MAX_VALUE);
            boolean flag = false;
            for (long t = 0; t < 400000; t++) {
                if (!flag && t >= second / step) {
                    flag = true;
                    Particle earth = algorithm.getParticles().get(1);

                    double EARTH_V_ANGLE = Math.atan(earth.getVel().getY() / earth.getVel().getX());
                    double EARTH_ANGLE = Math.atan2(earth.getPos().getY(), earth.getPos().getX());
                    double SPACESHIP_DISTANCE = earth.getPos().magnitude() + 1500;
                    double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
                    double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
                    double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
                    double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

                    algorithm.addParticle(new Particle(new Vector(SPACESHIP_X, SPACESHIP_Y), new Vector(SPACESHIP_VX, SPACESHIP_VY), 30, SPACESHIP_MASS));
                }

                algorithm.step();

                if (flag) {
                    Vector marsPos = algorithm.getParticles().get(2).getPos();
                    Vector spaceshipPos = algorithm.getParticles().get(3).getPos();
                    double distanceModule = Math.sqrt(Math.pow(spaceshipPos.getX() - marsPos.getX(), 2) + Math.pow(spaceshipPos.getY() - marsPos.getY(), 2));
                    if (distanceModule < minDistance.getModule()) {
                        minDistance.setX(spaceshipPos.getX() - marsPos.getX());
                        minDistance.setY(spaceshipPos.getY() - marsPos.getY());
                    }
                }
            }

            double module = minDistance.getModule();
            if (module < minGlobalDistance) {
                minGlobalDistance = module;
                minSecond = second;
            }
            System.out.println(module);
        }

        System.out.println("\nSecond: " + minSecond);
        System.out.println("Distance: " + minGlobalDistance);

    }
}
