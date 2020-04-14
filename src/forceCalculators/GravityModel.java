package forceCalculators;

import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class GravityModel implements ForcesCalculator {

    private static final double GRAVITATIONAL_CONSTANT = 0.00000000006674;

    private double magnitude(Vector pos){
        return Math.sqrt(Math.pow(pos.getX(), 2) + Math.pow(pos.getY(), 2));
    }

    private double radians(Vector pos1, Vector pos2){
        return Math.atan2(pos2.getY()-pos1.getY(), pos2.getX()-pos1.getX());
    }

    private double distance(Vector pos1, Vector pos2){
        return Math.sqrt(Math.pow(pos1.getX()-pos2.getX(), 2) + Math.pow(pos1.getY()-pos2.getY(), 2));
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> forces = new ArrayList<>();

        for(Particle particle : particleList){

            Particle newForce = new Particle(particle.getPos(), particle.getVel(), particle.getRadius(), particle.getMass());
            List<Vector> derivatives = newForce.getDerivatives();

            for(Particle otherParticle : particleList){
                double radians = radians(particle.getPos(), otherParticle.getPos());
                double otherMass = otherParticle.getMass();
                List<Vector> otherDerivatives = otherParticle.getDerivatives();

                double forceMagnitude;

                forceMagnitude = GRAVITATIONAL_CONSTANT*otherMass/Math.pow(distance(otherDerivatives.get(0),
                        derivatives.get(0)), 2);
                Vector r2 = new Vector(derivatives.get(2).getX()+forceMagnitude*Math.cos(radians),
                        derivatives.get(2).getX()+forceMagnitude*Math.sin(radians));

                forceMagnitude = GRAVITATIONAL_CONSTANT*otherMass/Math.pow(distance(otherDerivatives.get(0),
                        derivatives.get(0)), 4)*magnitude(derivatives.get(1));
                Vector r3 = new Vector(derivatives.get(2).getX()+forceMagnitude*Math.cos(radians),
                        derivatives.get(2).getX()+forceMagnitude*Math.sin(radians));

                forceMagnitude = GRAVITATIONAL_CONSTANT*otherMass/Math.pow(distance(otherDerivatives.get(0),
                        derivatives.get(0)), 4)*magnitude(derivatives.get(1));
                Vector r4 = new Vector(derivatives.get(2).getX()+forceMagnitude*Math.cos(radians),
                        derivatives.get(2).getX()+forceMagnitude*Math.sin(radians));
            }
        }

        return forces;
    }
}
