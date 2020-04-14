package forceCalculators;

import interfaces.DerivativesCalculator;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpringDerivatives implements DerivativesCalculator {

    private double k, gamma;

    public SpringDerivatives(double k, double gamma){
        this.k = k;
        this.gamma = gamma;
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> resultForces = new ArrayList<>();

        for(Particle particle : particleList){
            double mass = particle.getMass(), radius = particle.getRadius();

            List<Vector> derivatives = new ArrayList<>();

            Vector pos = particle.getPos(),
                    vel = particle.getVel();

            derivatives.add(pos);
            derivatives.add(vel);

            for(int i=2; i<=5; i++){
                derivatives.add(new Vector(
                        (-derivatives.get(i-2).getX()*this.k - derivatives.get(i-1).getX()*this.gamma)/mass,
                        (-derivatives.get(i-2).getY()*this.k - derivatives.get(i-1).getY()*this.gamma)/mass
                ));
            }

            resultForces.add(new Particle(derivatives, radius, mass));
        }

        return resultForces;
    }
}
