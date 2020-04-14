package algorithms;

import interfaces.DerivativesCalculator;
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

    public GearPredictorCorrectorOrder5(List<Particle> particles, ForcesCalculator forcesCalculator, double step, DerivativesCalculator derivativesCalculator){
        this.forcesCalculator = forcesCalculator;
        this.step = step;

        this.particleList = derivativesCalculator.calculate(particles);
    }


    @Override
    public void step() {

        //this.particleList = predictParticles();

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
                return 6;
            case 4:
                return 24;
            case 5:
                return 120;
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

            for(int j=0; j<=5 ; j++){
                correctedParticleDerivatives.add(correction(predictedParticleDerivatives.get(j), R2, j));
            }

            Particle assocParticle = this.particleList.get(i);
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

    private double taylor(int order, double[] coefs){
        double sum = 0;
        for(int n=0; n<order; n++){
            double coef = coefs[n];
            sum += coef*Math.pow(this.step, n)/fact(n);
        }
        return sum;
    }

    private List<Particle> predictParticles(){
        List<Particle> predictedParticles = new ArrayList<>();

        for (Particle particle : particleList) {
            List<Vector> currentParticleDerivatives = particle.getDerivatives();
            double mass = particle.getMass(), radius = particle.getRadius();

            List<Vector> derivatives = new ArrayList<>();

            derivatives.add(new Vector(
                    taylor(6, new double[]{currentParticleDerivatives.get(0).getX(), currentParticleDerivatives.get(1).getX(),
                            currentParticleDerivatives.get(2).getX(), currentParticleDerivatives.get(3).getX(),
                            currentParticleDerivatives.get(4).getX(), currentParticleDerivatives.get(5).getX()}),
                    taylor(6, new double[]{currentParticleDerivatives.get(0).getY(), currentParticleDerivatives.get(1).getY(),
                            currentParticleDerivatives.get(2).getY(), currentParticleDerivatives.get(3).getY(),
                            currentParticleDerivatives.get(4).getY(), currentParticleDerivatives.get(5).getY()})
            ));

            derivatives.add(new Vector(
                    taylor(5, new double[]{currentParticleDerivatives.get(1).getX(), currentParticleDerivatives.get(2).getX(),
                            currentParticleDerivatives.get(3).getX(), currentParticleDerivatives.get(4).getX(),
                            currentParticleDerivatives.get(5).getX()}),
                    taylor(5, new double[]{currentParticleDerivatives.get(1).getY(), currentParticleDerivatives.get(2).getY(),
                            currentParticleDerivatives.get(3).getY(), currentParticleDerivatives.get(4).getY(),
                            currentParticleDerivatives.get(5).getY()})
            ));

            derivatives.add(new Vector(
                    taylor(4, new double[]{currentParticleDerivatives.get(2).getX(), currentParticleDerivatives.get(3).getX(),
                            currentParticleDerivatives.get(4).getX(), currentParticleDerivatives.get(5).getX()}),
                    taylor(4, new double[]{currentParticleDerivatives.get(2).getY(), currentParticleDerivatives.get(3).getY(),
                            currentParticleDerivatives.get(4).getY(), currentParticleDerivatives.get(5).getY()})
            ));

            derivatives.add(new Vector(
                    taylor(3, new double[]{currentParticleDerivatives.get(3).getX(), currentParticleDerivatives.get(4).getX(),
                            currentParticleDerivatives.get(5).getX()}),
                    taylor(3, new double[]{currentParticleDerivatives.get(3).getY(), currentParticleDerivatives.get(4).getY(),
                            currentParticleDerivatives.get(5).getY()})
            ));

            derivatives.add(new Vector(
                    taylor(2, new double[]{currentParticleDerivatives.get(4).getX(), currentParticleDerivatives.get(5).getX()}),
                    taylor(2, new double[]{currentParticleDerivatives.get(4).getY(), currentParticleDerivatives.get(5).getY()})
            ));

            derivatives.add(new Vector(
                    taylor(1, new double[]{currentParticleDerivatives.get(5).getX()}),
                    taylor(1, new double[]{currentParticleDerivatives.get(5).getY()})
            ));

            predictedParticles.add(new Particle(derivatives, radius, mass));

        }

        return predictedParticles;
    }
}
