package forceCalculators;

import interfaces.ForcesCalculator;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class SocialModel implements ForcesCalculator {
    private final double A, B;
    private final double vd, tau;

    private final double wallY, holeWidth, width, height;

    private List<Vector> forces;

    public SocialModel(double A, double B, double vd, double tau, double wallY, double holeWidth, double width, double height){
        this.A = A;
        this.B = B;
        this.vd = vd;
        this.tau = tau;
        this.wallY = wallY;
        this.holeWidth = holeWidth;
        this.width = width;
        this.height = height;
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> result = new ArrayList<>(particleList.size());

        this.forces = new ArrayList<>(particleList.size());
        for(int i=0; i<particleList.size(); i++){
            forces.add(
                    new Vector(0,0)
            );
        }

        drivingForce(particleList);
        socialForce(particleList);
        clampToWalls(particleList);

        for(int i=0; i<particleList.size(); i++){
            Particle p = particleList.get(i);
            Vector f = this.forces.get(i);

            List<Vector> derivatives = new ArrayList<>(3);
            derivatives.add(p.getPos());
            derivatives.add(p.getVel());
            derivatives.add(Vector.scalarMultiplicationVector(1.0/p.getMass(), f));

            result.add(
                    new Particle(
                            derivatives,
                            p.getRadius(),
                            p.getMass()
                    )
            );
        }

        return result;
    }

    private Vector objectiveDirectionVector(Particle p){
        Vector objective;

        if(p.getPos().getY() >= this.wallY || (wallY - p.getPos().getY() < 1.0 && Math.abs(p.getPos().getX() - width/2.0) < holeWidth/2)){
            objective = new Vector(width/2.0, height);
        }else{
            objective = new Vector(width/2.0, wallY);
        }

        double distance = objective.distance(p.getPos());

        if(Double.compare(distance, 0.0) == 0)
            return null;

        return new Vector(
                (objective.getX() - p.getPos().getX())/distance,
                (objective.getY() - p.getPos().getY())/distance
        );
    }

    private void drivingForce(List<Particle> particles){
        for(int i=0; i<particles.size(); i++){
            Particle particle = particles.get(i);
            Vector f = this.forces.get(i);

            Vector directionVector = objectiveDirectionVector(particle);

            if(directionVector == null)
                return;

            f.sum(
                    new Vector(
                            particle.getMass()*(this.vd*directionVector.getX() - particle.getVel().getX())/this.tau,
                            particle.getMass()*(this.vd*directionVector.getY() - particle.getVel().getY())/this.tau
                    )
            );


        }
    }

    private void socialForce(List<Particle> particles){
        for(int i=0; i<particles.size(); i++){
            Particle p1 = particles.get(i);
            Vector f = this.forces.get(i);

            for(int j=0; j<particles.size(); j++){
                if(j==i)
                    continue;
                Particle p2 = particles.get(j);

                double distance = p1.getPos().distance(p2.getPos()) - p1.getRadius() - p2.getRadius();

                if(distance == 0)
                    continue;

                double ex = (p2.getPos().getX() - p1.getPos().getX())/distance,
                        ey = (p2.getPos().getY() - p1.getPos().getY())/distance;

                double accMagnitude = this.A*Math.exp(-distance/this.B);
                f.sum(new Vector(
                        -accMagnitude*ex,
                        -accMagnitude*ey
                ));
            }

        }
    }

    private void clampToWalls(List<Particle> particles){
        for(int i=0; i<particles.size(); i++){
            Particle p1 = particles.get(i);
            Vector f = this.forces.get(i);

            double x = p1.getPos().getX();

            double accMagnitude, ex, ey;
            if(x < width/2 - holeWidth/2 || x > width/2 + holeWidth/2){
                double distance = p1.getPos().distance(new Vector(x, wallY)) - p1.getRadius();

                if(distance == 0)
                    continue;

                ex = 0;

                ey = (wallY - p1.getPos().getY())/distance;

                accMagnitude = this.A*Math.exp(-distance/this.B);

            }else{
                double closestX = x - width/2 - holeWidth/2 < width/2 + holeWidth/2 - x ? width/2 - holeWidth/2 : width/2 + holeWidth/2;

                double distance = p1.getPos().distance(new Vector(closestX, wallY)) - p1.getRadius();

                if(distance == 0)
                    continue;

                ex = (closestX - p1.getPos().getX())/distance;

                ey = (wallY - p1.getPos().getY())/distance;

                accMagnitude = this.A*Math.exp(-distance/this.B);
            }

            f.sum(new Vector(
                    -accMagnitude*ex,
                    -accMagnitude*ey
            ));
        }
    }
}
