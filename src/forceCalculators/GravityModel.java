package forceCalculators;

import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class GravityModel implements ForcesCalculator {

    // km * kg^-1 * s^2
    private static final double GRAVITATIONAL_CONSTANT = 6.674e-20;

    private double distance(Vector pos1, Vector pos2){
        return Math.sqrt(squaredDistance(pos1, pos2));
    }

    private double squaredDistance(Vector pos1, Vector pos2){
        return Math.pow(pos1.getX()-pos2.getX(), 2) + Math.pow(pos1.getY()-pos2.getY(), 2);
    }

    @Override
    public List<Particle> calculate(List<Particle> particleList) {
        List<Particle> forces = new ArrayList<>();

        Vector[][] forcesMatrix = new Vector[particleList.size()][particleList.size()];
        for(int i=0; i<particleList.size(); i++){
            for(int j=0; j<particleList.size(); j++){
                if(i==j){
                    forcesMatrix[i][j] = new Vector(0, 0);
                }else{
                    Particle iParticle = particleList.get(i), jParticle = particleList.get(j);

                    double ex = (jParticle.getPos().getX() - iParticle.getPos().getX())/distance(jParticle.getPos(), iParticle.getPos()),
                            ey = (jParticle.getPos().getY() - iParticle.getPos().getY())/distance(jParticle.getPos(), iParticle.getPos());

                    double forceModule = iParticle.getMass() * GRAVITATIONAL_CONSTANT * jParticle.getMass() / (squaredDistance(iParticle.getPos(), jParticle.getPos()));

                    forcesMatrix[i][j] = new Vector(ex*forceModule, ey*forceModule);
                }
            }
        }

        for(int i=0; i<particleList.size(); i++){
            Particle particle = particleList.get(i);
            List<Vector> derivatives = new ArrayList<>();

            derivatives.add(particle.getPos());
            derivatives.add(particle.getVel());

            Vector acc = new Vector(0,0);
            for(int j=0; j<forcesMatrix.length; j++){
                acc.sum(forcesMatrix[i][j]);
            }

            derivatives.add(Vector.scalarMultiplicationVector(1/particle.getMass(),acc));

            forces.add(new Particle(derivatives, particle.getRadius(), particle.getMass()));
        }

        return forces;
    }
}
