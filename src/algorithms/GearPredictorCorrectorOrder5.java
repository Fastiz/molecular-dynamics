package algorithms;

import interfaces.ForcesCalculator;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;
import models.Vector;

import java.util.ArrayList;
import java.util.List;

public class GearPredictorCorrectorOrder5 implements TemporalStepAlgorithmInterface {
    private ForcesCalculator forcesCalculator;
    private List<Particle> particleList;
    private double step;
    private static final double[] ALPHAS = new double[]{3.0/16, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};

    public GearPredictorCorrectorOrder5(List<Particle> particles, ForcesCalculator forcesCalculator, double step){
        this.forcesCalculator = forcesCalculator;
        this.step = step;

        this.particleList = particles;

        setInitialValuesForParticles();
    }

    private void setInitialValuesForParticles(){
        List<Particle> forces = this.forcesCalculator.calculate(this.particleList);
        for(int i=0; i<forces.size(); i++){
            Particle force = forces.get(i);
            Particle particle = this.particleList.get(i);

            force.setPosition(particle.getPos());
            force.setVel(particle.getVel());

        }

        this.particleList = forces;
    }

    @Override
    public void step() {

        List<Particle> predictedParticles = predictParticles();

        List<Vector> calculatedR2 = calculateR2(predictedParticles);

        this.particleList = correctParticles(predictedParticles, calculatedR2);

    }

    @Override
    public List<Particle> getParticles() {
        return this.particleList;
    }

    private int fact(int n){
        switch (n){
            case 0:
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 9;
            case 4:
                return 16;
            case 5:
                return 25;
        }
        return -1;
    }

    private Vector correction(Vector derivative, Vector R2, int order){
        double x, y;

        int fact = fact(order);

        x = derivative.getX() + ALPHAS[order]*R2.getX()*fact/Math.pow(this.step, order);
        y = derivative.getY() + ALPHAS[order]*R2.getY()*fact/Math.pow(this.step, order);

        return new Vector(x, y);
    }

    private List<Particle> correctParticles(List<Particle> predictedParticles, List<Vector> calculatedR2){
        List<Particle> correctedParticles = new ArrayList<>();
        for(int i=0; i<predictedParticles.size(); i++){
            List<Vector> correctedParticleDerivatives = new ArrayList<>();
            List<Vector> predictedParticleDerivatives = predictedParticles.get(i).getDerivatives();
            Vector R2 = calculatedR2.get(i);

            Particle assocParticle = this.particleList.get(i);
            for(int j=0; j<=5 ; j++){
                correctedParticleDerivatives.add(correction(predictedParticleDerivatives.get(j), R2, j));
            }

            correctedParticles.add(new Particle(correctedParticleDerivatives, assocParticle.getRadius(), assocParticle.getMass()));
        }
        return correctedParticles;
    }

    private List<Vector> calculateR2(List<Particle> predictedParticles){
        List<Vector> calculatedR2 = new ArrayList<>();

        List<Particle> newPredictedParticles = this.forcesCalculator.calculate(predictedParticles);

        for(int i=0; i<predictedParticles.size(); i++){
            Particle predictedParticle = predictedParticles.get(i);
            Particle newPredictedParticle = newPredictedParticles.get(i);

            double dx, dy;

            dx = newPredictedParticle.getDerivatives().get(2).getX() - predictedParticle.getDerivatives().get(2).getX();
            dy = newPredictedParticle.getDerivatives().get(2).getY() - predictedParticle.getDerivatives().get(2).getY();

            calculatedR2.add(new Vector(dx*Math.pow(this.step, 2)/2, dy*Math.pow(this.step, 2)/2));
        }

        return calculatedR2;
    }

    private List<Particle> predictParticles(){
        List<Particle> predictedParticles = this.forcesCalculator.calculate(particleList);

        for(int i=0; i<particleList.size(); i++){
            List<Vector> currentParticleDerivatives = particleList.get(i).getDerivatives();
            Particle predictedParticle = predictedParticles.get(i);

            List<Vector> derivatives = predictedParticle.getDerivatives();
            double x, y;

            x = currentParticleDerivatives.get(1).getX() + currentParticleDerivatives.get(2).getX()*this.step +
                    currentParticleDerivatives.get(3).getX()*Math.pow(this.step, 2)/4 + currentParticleDerivatives.get(4).getX()*Math.pow(this.step, 3)/9 +
                    currentParticleDerivatives.get(5).getX()*Math.pow(this.step, 4)/16;

            y = currentParticleDerivatives.get(1).getY() + currentParticleDerivatives.get(2).getY()*this.step +
                    currentParticleDerivatives.get(3).getY()*Math.pow(this.step, 2)/4 + currentParticleDerivatives.get(4).getY()*Math.pow(this.step, 3)/9 +
                    currentParticleDerivatives.get(5).getY()*Math.pow(this.step, 4)/16;

            derivatives.set(1, new Vector(x, y));

            x = currentParticleDerivatives.get(0).getX() + currentParticleDerivatives.get(1).getX()*this.step +
                    currentParticleDerivatives.get(2).getX()*Math.pow(this.step, 2)/4 + currentParticleDerivatives.get(3).getX()*Math.pow(this.step, 3)/9 +
                    currentParticleDerivatives.get(4).getX()*Math.pow(this.step, 4)/16 + currentParticleDerivatives.get(5).getX()*Math.pow(this.step, 5)/25;

            y = currentParticleDerivatives.get(0).getY() + currentParticleDerivatives.get(1).getY()*this.step +
                    currentParticleDerivatives.get(2).getY()*Math.pow(this.step, 2)/4 + currentParticleDerivatives.get(3).getY()*Math.pow(this.step, 3)/9 +
                    currentParticleDerivatives.get(4).getY()*Math.pow(this.step, 4)/16 + currentParticleDerivatives.get(5).getY()*Math.pow(this.step, 5)/25;

            derivatives.set(0, new Vector(x, y));
        }

        return predictedParticles;
    }
}
