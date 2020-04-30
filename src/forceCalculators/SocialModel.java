package forceCalculators;

import interfaces.ForcesCalculator;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SocialModel implements ForcesCalculator {
    private final double A, B;
    private final double vd, tau;

    public SocialModel(double A, double B, double vd, double tau){
        this.A = A;
        this.B = B;
        this.vd = vd;
        this.tau = tau;
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> result = particleList.stream().map(this::initializeParticle).collect(Collectors.toList());

        drivingForce(result);
        socialForce(result);
        clampToWalls(result);

        return result;
    }

    private Particle initializeParticle(Particle p){
        List<Vector> derivatives = new ArrayList<>();
        derivatives.add(p.getPos());
        derivatives.add(p.getVel());
        derivatives.add(new Vector(0, 0));
        return new Particle(derivatives, p.getRadius(), p.getMass());
    }

    private Vector objectiveDirectionVector(Particle p){
        Vector objective;

        objective = new Vector(200, 200);

        double distance = objective.distance(p.getPos());
        return new Vector(
                (objective.getX() - p.getPos().getX())/distance,
                (objective.getY() - p.getPos().getY())/distance
        );
    }

    private void drivingForce(List<Particle> particles){
        for(Particle particle : particles){
            Vector acc = particle.getAcel();

            Vector directionVector = objectiveDirectionVector(particle);
            acc.sum(
                    new Vector(
                            (this.vd*directionVector.getX() - particle.getVel().getX())/this.tau,
                            (this.vd*directionVector.getY() - particle.getVel().getY())/this.tau
                    )
            );


        }
    }

    private void socialForce(List<Particle> particles){
        for(int i=0; i<particles.size(); i++){
            Particle p1 = particles.get(i);

            Vector acc = p1.getAcel();
            for(int j=0; j<particles.size(); j++){
                if(j==i)
                    continue;
                Particle p2 = particles.get(j);

                double distance = p1.getPos().distance(p2.getPos()) - p1.getRadius() - p2.getRadius();
                distance = distance < 0 ? 0 : distance;

                double ex = (p2.getPos().getX() - p1.getPos().getX())/distance,
                        ey = (p2.getPos().getY() - p1.getPos().getY())/distance;

                double accMagnitude = this.A*Math.exp(-distance/this.B)/p1.getMass();
                acc.sum(new Vector(
                        accMagnitude*ex,
                        accMagnitude*ey
                ));
            }

        }
    }

    private void clampToWalls(List<Particle> particles){

    }
}
