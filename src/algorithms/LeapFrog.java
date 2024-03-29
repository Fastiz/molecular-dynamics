package algorithms;

import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class LeapFrog implements TemporalStepAlgorithmInterface {
    private double TEMP_STEP;
    private double HALF_TEMP_STEP;
    private List<Vector> halfPreviousVels;
    private List<Vector> halfNextVels;
    private List<Particle> particles;
    private ForcesCalculator forcesCalculator;

    public LeapFrog(List<Particle> particles, ForcesCalculator forcesCalculator, double step) {
        halfNextVels = new ArrayList<>(particles.size());
        this.forcesCalculator = forcesCalculator;
        this.particles = particles;
        this.TEMP_STEP = step;
        HALF_TEMP_STEP = TEMP_STEP / 2;

        this.particles = forcesCalculator.calculate(particles);
        estimatePreviousSpeeds();
        setNextSpeeds();
    }

    public void step() {
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Vector newVel = halfNextVels.get(i);
            particle.setPosition(calculateNextPos(particle, newVel));
        }
        setCurrentSpeedAndForces();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    private void setNextSpeeds() {
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Vector newVel = calculateNextVel(particle, halfPreviousVels.get(i));
            halfNextVels.add(newVel);
        }
    }

    private void setCurrentSpeedAndForces() {
        halfPreviousVels = halfNextVels;
        halfNextVels = new ArrayList<>(particles.size());
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Vector newVel = calculateNextVel(particle, halfPreviousVels.get(i));
            halfNextVels.add(newVel);
            double vx = (halfPreviousVels.get(i).getX() + newVel.getX()) / 2;
            double vy = (halfPreviousVels.get(i).getY() + newVel.getY()) / 2;
            particles.get(i).setVel(vx, vy);
        }

        particles = forcesCalculator.calculate(particles);
    }

    private Vector calculateNextVel(Particle particle, Vector previousVel) {
        double vx = previousVel.getX() + (TEMP_STEP * particle.getAcel().getX());
        double vy = previousVel.getY() + (TEMP_STEP * particle.getAcel().getY());
        return new Vector(vx, vy);
    }

    private Vector calculateNextPos(Particle particle, Vector newVel) {
        double x = particle.getPos().getX() + (TEMP_STEP * newVel.getX());
        double y = particle.getPos().getY() + (TEMP_STEP * newVel.getY());
        return new Vector(x, y);
    }

    private void estimatePreviousSpeeds() {
        halfPreviousVels = new ArrayList<>(particles.size());
        for(Particle particle : particles) {
            double vx = particle.getVel().getX() - (HALF_TEMP_STEP * particle.getAcel().getX());
            double vy = particle.getVel().getY() - (HALF_TEMP_STEP * particle.getAcel().getY());
            halfPreviousVels.add(new Vector(vx, vy));
        }
    }
}
