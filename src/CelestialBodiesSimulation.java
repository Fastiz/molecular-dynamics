import algorithms.Beeman;
import algorithms.SimpleBeeman;
import forceCalculators.GravityModel;
import models.Particle;
import models.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CelestialBodiesSimulation {
    private static final double JUPITER_X = 1.8423672321997e8;
    private static final double JUPITER_Y = -7.5460471617435e8;
    private static final double JUPITER_VX = 12.5486;
    private static final double JUPITER_VY = 3.7187;
    private static final double JUPITER_MASS = 1898.13e24;

    private static final double MARS_X = -2.4712389774953e7;
    private static final double MARS_Y = -2.1837372294411e8;
    private static final double MARS_VX = 24.991186369972820103;
    private static final double MARS_VY = -0.64123285744192605762;
    private static final double MARS_MASS = 6.4171e23;
    private static final double MARS_RATIO = 3389.92;

    private static final double MOON_X = -1.4397153695201e8;
    private static final double MOON_Y = -4.2136609701526e7;
    private static final double MOON_VX = 7.69702;
    private static final double MOON_VY = -29.7485;
    private static final double MOON_MASS = 7.349e22;

    private static final double EARTH_X = -1.4362322641829e8;
    private static final double EARTH_Y = -4.2221842462959e7;
    private static final double EARTH_ANGLE = Math.atan2(EARTH_Y, EARTH_X);
    private static final double EARTH_DISTANCE = Math.abs(EARTH_X / Math.cos(EARTH_ANGLE));
    private static final double EARTH_VX = 7.9179041699407207489;
    private static final double EARTH_VY = -28.678710520938155241;
    private static final double EARTH_V_ANGLE = Math.atan2(EARTH_VY,  EARTH_VX);
    private static final double EARTH_V = EARTH_VX / Math.cos(EARTH_V_ANGLE);
    private static final double EARTH_MASS = 5.97219e24;

    private static final double VENUS_X = -1.0016859425279e8;
    private static final double VENUS_Y = 3.8671267845279e7;
    private static final double VENUS_VX = -12.7743;
    private static final double VENUS_VY = -32.8327;
    private static final double VENUS_MASS = 48.685e23;

    private static final double MERCURY_X = 1.726949067612e7;
    private static final double MERCURY_Y = -6.5227786778893e7;
    private static final double MERCURY_VX = 37.3234;
    private static final double MERCURY_VY = 14.9515;
    private static final double MERCURY_MASS = 3.302e23;

    private static final double SUN_MASS = 1.988500e30;

    private static final double SPACESHIP_MASS = 2.0e5;
    private static final double SPACESHIP_DISTANCE = EARTH_DISTANCE + 1500 + 6371.01;
    private static final double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
    private static final double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
    private static final double SPACESHIP_V = 8.0 + 7.12;
    private static final double SPACESHIP_VX = SPACESHIP_V * Math.cos(EARTH_V_ANGLE);
    private static final double SPACESHIP_VY = SPACESHIP_V * Math.sin(EARTH_V_ANGLE);

    public void runAnimation() throws IOException {
        List<Particle> particlesList = getPlanetList();

        double step = 50;
        SimpleBeeman algorithm = new SimpleBeeman(particlesList, new GravityModel(), step);


        double maxMagnitude = 0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("dynamic_file"))){
            boolean flag = false;
            for(long t=0; t< 8467200 * 16 / step; t++){
                if (!flag && t >= 8467200 / step) {
                    flag = true;
                    Particle earth = algorithm.getParticles().get(1);
                    algorithm.addParticle(generateSpaceship(earth));
                }
                if(t % 100 == 0) {
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

    public void searchOptimalTime() throws IOException {
        List<Particle> particlesList = getPlanetList();


        (new File("results/")).mkdir();
        (new File("results/gravity/")).mkdir();

        Utils.deleteFiles("results/gravity/");

        int step = 50;
        int TIME_STEP = 24 * 60 * 60 / step;
        int TIME_ITERATIONS = 700;

        for(int it=0; it<=TIME_ITERATIONS; it++){
            System.out.println("Simulating launch with offset: " + TIME_STEP*it*step +" s and index: " + it +".");

            try(BufferedWriter bf = new BufferedWriter(new FileWriter("results/gravity/sim"+it))){
                bf.write((int)Math.ceil(it * TIME_STEP / 1000.0) + "\n");

                Beeman algorithm = new Beeman(new ArrayList<>(particlesList), new GravityModel(), step);
                Vector minDistance = new Vector(Double.MAX_VALUE, Double.MAX_VALUE);
                boolean spaceshipTookOff = false;
                boolean alreadyPassedMars = false;

                for(long t=0; !alreadyPassedMars; t++){
                    if(!spaceshipTookOff && t >= it*TIME_STEP){
                        spaceshipTookOff = true;
                        Particle earth = algorithm.getParticles().get(1);
                        algorithm.addParticle(generateSpaceship(earth));
                    }

                    if (spaceshipTookOff) {
                        List<Particle> particleList = algorithm.getParticles();
                        Vector marsPos = particleList.get(2).getPos();
                        Vector spaceshipPos = particleList.get(particleList.size()-1).getPos();
                        double distanceModule = Math.sqrt(Math.pow(spaceshipPos.getX() - marsPos.getX(), 2) + Math.pow(spaceshipPos.getY() - marsPos.getY(), 2));
                        if (distanceModule < minDistance.getModule()) {
                            minDistance.setX(spaceshipPos.getX() - marsPos.getX());
                            minDistance.setY(spaceshipPos.getY() - marsPos.getY());
                        }

                        if(marsPos.magnitude() * 1.25 < spaceshipPos.magnitude())
                            alreadyPassedMars = true;
                    }

                    if(t % 1000 == 0) {
                        bf.write("#T" + t + "\n");
                        List<Particle> current = algorithm.getParticles();
                        for (Particle particle : current) {
                            Vector pos = particle.getPos();
                            Vector vel = particle.getVel();

                            bf.write(pos.getX() + " " + pos.getY() + " " + vel.getX() + " " + vel.getY() + "\n");
                        }

                    }

                    algorithm.step();
                }
                System.out.println("Min Distance: " + minDistance.magnitude());
            }
        }
    }

    public void runAlternativeSystemAnimation() throws IOException{
        List<Particle> particlesList = getAlternativePlanetsList();

        int step = 50;
        SimpleBeeman algorithm = new SimpleBeeman(particlesList, new GravityModel(), step);

        Particle earth = algorithm.getParticles().get(1);
        algorithm.addParticle(generateSpaceship(earth));

        double maxMagnitude = 0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("dynamic_file"))){
            for(long t=0; t< 82000000; t++){

                if(t % 1500 == 0) {
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

    public void searchLowerTravelTime(int seconds) throws IOException {

        List<Particle> particlesList = getPlanetList();

        (new File("results")).mkdir();
        (new File("results/velocity/")).mkdir();

        Utils.deleteFiles("results/velocity/");
        Map<Double, Double> map = new HashMap<>();
        double velocityStep = 0.01;
        double step = 0.1;

        try (BufferedWriter bf = new BufferedWriter(new FileWriter("results/velocity/timePerVelocity"))) {
            for (double spaceshipVelocity = SPACESHIP_V - 0.01; spaceshipVelocity < 8.01 + 7.12; spaceshipVelocity += velocityStep) {

                SimpleBeeman algorithm = new SimpleBeeman(new ArrayList<>(particlesList), new GravityModel(), step);

                boolean flag = false;
                boolean cutSimulation = false;
                System.out.println("Simulation with speed: " + spaceshipVelocity);
                for (int t = 0; !cutSimulation; t++) {
                    if (!flag && t >= seconds / step) {
                        flag = true;
                        Particle earth = algorithm.getParticles().get(1);
                        algorithm.addParticle(generateSpaceship(earth, spaceshipVelocity));
                    }

                    algorithm.step();

                    if(flag) {
                        Vector marsPos = algorithm.getParticles().get(2).getPos();
                        Vector spaceshipPos = algorithm.getParticles().get(3).getPos();
                        double distanceModule = Math.sqrt(Math.pow(spaceshipPos.getX() - marsPos.getX(), 2) + Math.pow(spaceshipPos.getY() - marsPos.getY(), 2));
                        if (distanceModule < MARS_RATIO * 3) {
                            map.put(spaceshipVelocity, t * step);
                            cutSimulation = true;
                        } else if (spaceshipPos.magnitude() > marsPos.magnitude() * 1.5) {
                            cutSimulation = true;
                        }
                    }
                }
            }
            System.out.println(map);
        }
    }

    private Particle generateSpaceship(Particle earth) {
        double EARTH_V_ANGLE = Math.atan2(earth.getVel().getY(), earth.getVel().getX());
        double EARTH_ANGLE = Math.atan2(earth.getPos().getY(), earth.getPos().getX());
        double SPACESHIP_DISTANCE = earth.getPos().magnitude() + 1500 + 6371.01;
        double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
        double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
        double SPACESHIP_VX = (SPACESHIP_V * Math.cos(EARTH_V_ANGLE)) + (-Math.sin(EARTH_ANGLE) * earth.getVel().magnitude());
        double SPACESHIP_VY = (SPACESHIP_V * Math.sin(EARTH_V_ANGLE)) + (Math.cos(EARTH_ANGLE) * earth.getVel().magnitude());

        return new Particle(new Vector(SPACESHIP_X, SPACESHIP_Y), new Vector(SPACESHIP_VX, SPACESHIP_VY), 30, SPACESHIP_MASS);
    }

    private Particle generateSpaceship(Particle earth, double spaceshipVel) {
        double EARTH_V_ANGLE = Math.atan2(earth.getVel().getY(), earth.getVel().getX());
        double EARTH_ANGLE = Math.atan2(earth.getPos().getY(), earth.getPos().getX());
        double SPACESHIP_DISTANCE = earth.getPos().magnitude() + 1500 + 6371.01;
        double SPACESHIP_X = SPACESHIP_DISTANCE * Math.cos(EARTH_ANGLE);
        double SPACESHIP_Y = SPACESHIP_DISTANCE * Math.sin(EARTH_ANGLE);
        double SPACESHIP_VX = (spaceshipVel * Math.cos(EARTH_V_ANGLE)) + (-Math.sin(EARTH_ANGLE) * earth.getVel().magnitude());
        double SPACESHIP_VY = (spaceshipVel * Math.sin(EARTH_V_ANGLE)) + (Math.cos(EARTH_ANGLE) * earth.getVel().magnitude());

        return new Particle(new Vector(SPACESHIP_X, SPACESHIP_Y), new Vector(SPACESHIP_VX, SPACESHIP_VY), 30, SPACESHIP_MASS);
    }

    private List<Particle> getAlternativePlanetsList() {
        List<Particle> particlesList = getPlanetList();
        particlesList.add(new Particle(new Vector(VENUS_X, VENUS_Y), new Vector(VENUS_VX, VENUS_VY), 30, VENUS_MASS));
        particlesList.add(new Particle(new Vector(MARS_X * 3, MARS_Y * 3), new Vector(JUPITER_VX, JUPITER_VY), 30, JUPITER_MASS));
        particlesList.add(new Particle(new Vector(MERCURY_X, MERCURY_Y), new Vector(MERCURY_VX, MERCURY_VY), 30, MERCURY_MASS));
        particlesList.add(new Particle(new Vector(MOON_X, MOON_Y), new Vector(MOON_VX, MOON_VY), 30, MOON_MASS));
        return particlesList;
    }

    private List<Particle> getPlanetList() {
        List<Particle> particlesList = new ArrayList<>();
        particlesList.add(new Particle(new Vector(0, 0), new Vector(0, 0), 20, SUN_MASS));
        particlesList.add(new Particle(new Vector(EARTH_X, EARTH_Y), new Vector(EARTH_VX, EARTH_VY), 30, EARTH_MASS));
        particlesList.add(new Particle(new Vector(MARS_X, MARS_Y), new Vector(MARS_VX, MARS_VY), 30, MARS_MASS));
        return particlesList;
    }
}
