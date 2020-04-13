package forceCalculators;

import interfaces.ForcesCalculator;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpringModel implements ForcesCalculator {

    private double k, gamma;

    public SpringModel(double k, double gamma){
        this.k = k;
        this.gamma = gamma;
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> resultForces = new ArrayList<>();

        for(Particle particle : particleList){
            double mass = particle.getMass(), radius = particle.getRadius();

            List<Vector> derivatives = new ArrayList<>();

            //setting to 0 position and velocity
            derivatives.add(particle.getPos());
            derivatives.add(particle.getVel());

            double x, y;
            x = -particle.getDerivatives().get(0).getX()*this.k - particle.getDerivatives().get(1).getX()*this.gamma;
            y = -particle.getDerivatives().get(0).getY()*this.k - particle.getDerivatives().get(1).getY()*this.gamma;
            derivatives.add(new Vector(x/mass, y/mass));

            for(int i=3; i<=5; i++){
                x = -derivatives.get(i-2).getX()*this.k - derivatives.get(i-1).getX()*this.gamma;
                y = -derivatives.get(i-2).getY()*this.k - derivatives.get(i-1).getY()*this.gamma;
                derivatives.add(new Vector(x/mass, y/mass));
            }

            resultForces.add(new Particle(derivatives, radius, mass));
        }

        return resultForces;
    }
}
