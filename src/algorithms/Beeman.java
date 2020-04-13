package algorithms;

import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class Beeman implements TemporalStepAlgorithmInterface {

    private static double TEMP_STEP = 0;
    private List<Particle> previousParticles;
    private List<Particle> particles;
    private ForcesCalculator forcesCalculator;

    public Beeman(ForcesCalculator forcesCalculator, List<Particle> particles) {
        this.forcesCalculator = forcesCalculator;
        this.particles = particles;
        estimatePrevious();
    }

    public void step() {
        List<Particle> newParticles = new ArrayList<>(particles.size());
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Particle previousParticle = previousParticles.get(i);
            Vector newPos = calculateNextPos(particle, previousParticle);
            Vector predictedVel = predictNextVel(particle, previousParticle);
            Particle newParticle = new Particle(newPos, predictedVel);
            newParticles.add(newParticle);
        }

        newParticles = forcesCalculator.calculate(newParticles);

        for(int i = 0; i < newParticles.size(); i++) {
            setNextVel(newParticles.get(i), particles.get(i), previousParticles.get(i));
        }

        previousParticles = particles;
        particles = newParticles;
    }

    private Vector calculateNextPos(Particle particle, Particle previousParticle) {
        double x = particle.getPos().getX() + (particle.getVel().getX() * TEMP_STEP) + (2 * particle.getAcel().getX() * Math.pow(TEMP_STEP, 2) / 3) - (previousParticle.getAcel().getX() * Math.pow(TEMP_STEP, 2) / 6);
        double y = particle.getPos().getY() + (particle.getVel().getY() * TEMP_STEP) + (2 * particle.getAcel().getY() * Math.pow(TEMP_STEP, 2) / 3) - (previousParticle.getAcel().getY() * Math.pow(TEMP_STEP, 2) / 6);
        return new Vector(x, y);
    }

    private Vector predictNextVel(Particle particle, Particle previousParticle) {
        double vx = particle.getVel().getX() + (3 * particle.getAcel().getX() * TEMP_STEP / 2) - (previousParticle.getAcel().getX() * TEMP_STEP / 2);
        double vy = particle.getVel().getY() + (3 * particle.getAcel().getY() * TEMP_STEP / 2) - (previousParticle.getAcel().getY() * TEMP_STEP / 2);
        return new Vector(vx, vy);
    }

    private void setNextVel(Particle newParticle, Particle particle, Particle previousParticle) {
        double vx = particle.getVel().getX() + (newParticle.getAcel().getX() * TEMP_STEP / 3) + (5 * particle.getAcel().getX() * TEMP_STEP / 6) - (previousParticle.getAcel().getX() * TEMP_STEP / 6);
        double vy = particle.getVel().getY() + (newParticle.getAcel().getY() * TEMP_STEP / 3) + (5 * particle.getAcel().getY() * TEMP_STEP / 6) - (previousParticle.getAcel().getY() * TEMP_STEP / 6);
        newParticle.setVel(vx, vy);
    }

    private void estimatePrevious() {
        previousParticles = new ArrayList<>();
        particles = forcesCalculator.calculate(particles);
        for(Particle particle : particles) {
            List<Vector> derivates = new ArrayList<>();
            double x = particle.getPos().getX() - (TEMP_STEP * particle.getVel().getX()) + (Math.pow(TEMP_STEP, 2) * particle.getAcel().getX() /  2);
            double y = particle.getPos().getY() - (TEMP_STEP * particle.getVel().getY()) + (Math.pow(TEMP_STEP, 2) * particle.getAcel().getY() / 2);
            derivates.add(new Vector(x,y));
            double vx = particle.getVel().getX() - (TEMP_STEP * particle.getAcel().getX());
            double vy = particle.getVel().getY() - (TEMP_STEP * particle.getAcel().getY());
            derivates.add(new Vector(vx,vy));
            previousParticles.add(new Particle(derivates));
        }

        previousParticles = forcesCalculator.calculate(previousParticles);
    }
}
