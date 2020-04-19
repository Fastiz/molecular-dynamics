package algorithms;

import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class Beeman implements TemporalStepAlgorithmInterface {

    private double TEMP_STEP;
    private List<Particle> previousParticles;
    private List<Particle> particles;
    private ForcesCalculator forcesCalculator;

    public Beeman(List<Particle> particles, ForcesCalculator forcesCalculator, double step) {
        this.forcesCalculator = forcesCalculator;
        this.particles = particles;
        this.TEMP_STEP = step;
        estimateAllPrevious();
    }

    public void step() {
        List<Particle> newParticles = new ArrayList<>(particles.size());
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Particle previousParticle = previousParticles.get(i);
            Vector newPos = calculateNextPos(particle, previousParticle);
            Vector predictedVel = predictNextVel(particle, previousParticle);
            Particle newParticle = new Particle(newPos, predictedVel, particle.getRadius(), particle.getMass());
            newParticles.add(newParticle);
        }

        newParticles = forcesCalculator.calculate(newParticles);

        for(int i = 0; i < newParticles.size(); i++) {
            setNextVel(newParticles.get(i), particles.get(i), previousParticles.get(i));
        }

        previousParticles = particles;
        particles = newParticles;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    private Vector calculateNextPos(Particle particle, Particle previousParticle) {
        double x = nextPos(particle.getPos().getX(), particle.getVel().getX(), particle.getAcel().getX(), previousParticle.getAcel().getX());
        double y = nextPos(particle.getPos().getY(), particle.getVel().getY(), particle.getAcel().getY(), previousParticle.getAcel().getY());
        return new Vector(x, y);
    }

    private double nextPos(double pos, double vel, double acel, double previousAcel) {
        return pos + (vel * TEMP_STEP) + (2 * acel * Math.pow(TEMP_STEP, 2) / 3) - (previousAcel * Math.pow(TEMP_STEP, 2) / 6);
    }

    private Vector predictNextVel(Particle particle, Particle previousParticle) {
        double vx = predictVel(particle.getVel().getX(), particle.getAcel().getX(), previousParticle.getAcel().getX());
        double vy = predictVel(particle.getVel().getY(), particle.getAcel().getY(), previousParticle.getAcel().getY());
        return new Vector(vx, vy);
    }

    private double predictVel(double vel, double acel, double previousAcel) {
        return vel + (3 * acel * TEMP_STEP / 2) - (previousAcel * TEMP_STEP / 2);
    }

    private void setNextVel(Particle newParticle, Particle particle, Particle previousParticle) {
        double vx = nextVel(particle.getVel().getX(), newParticle.getAcel().getX(), particle.getAcel().getX(), previousParticle.getAcel().getX());
        double vy = nextVel(particle.getVel().getY(), newParticle.getAcel().getY(), particle.getAcel().getY(), previousParticle.getAcel().getY());
        newParticle.setVel(vx, vy);
    }

    private double nextVel(double vel, double newAcel, double acel, double previousAcel) {
        return vel + (newAcel * TEMP_STEP / 3) + (5 * acel * TEMP_STEP / 6) - (previousAcel * TEMP_STEP / 6);
    }

    private void addParticle(Particle particle) {
        particles.add(particle);
        addEstimation(particle);
    }

    private void addEstimation(Particle particle) {
        particles = forcesCalculator.calculate(particles);
        previousParticles.add(new Particle(getDerivatives(particle), particle.getRadius(), particle.getMass()));
        previousParticles = forcesCalculator.calculate(previousParticles);
    }

    private void estimateAllPrevious() {
        previousParticles = new ArrayList<>();
        particles = forcesCalculator.calculate(particles);
        for(Particle particle : particles) {
            previousParticles.add(new Particle(getDerivatives(particle), particle.getRadius(), particle.getMass()));
        }

        previousParticles = forcesCalculator.calculate(previousParticles);
    }

    private List<Vector> getDerivatives(Particle particle) {
        List<Vector> derivatives = new ArrayList<>();
        double x = particle.getPos().getX() - (TEMP_STEP * particle.getVel().getX()) + (Math.pow(TEMP_STEP, 2) * particle.getAcel().getX() /  2);
        double y = particle.getPos().getY() - (TEMP_STEP * particle.getVel().getY()) + (Math.pow(TEMP_STEP, 2) * particle.getAcel().getY() / 2);
        derivatives.add(new Vector(x,y));
        double vx = particle.getVel().getX() - (TEMP_STEP * particle.getAcel().getX());
        double vy = particle.getVel().getY() - (TEMP_STEP * particle.getAcel().getY());
        derivatives.add(new Vector(vx,vy));
        return derivatives;
    }
}
