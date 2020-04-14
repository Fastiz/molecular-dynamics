import algorithms.Beeman;
import algorithms.GearPredictorCorrectorOrder5;
import algorithms.LeapFrog;
import forceCalculators.SpringDerivatives;
import forceCalculators.SpringModel;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        double K = 10000, GAMMA = 100, MASS = 70, STEP=0.001;

        List<Particle> particles = new ArrayList<>();
        particles.add(new Particle(1, 0, -GAMMA/(2*MASS), 0, 1, MASS));

        TemporalStepAlgorithmInterface algorithm = new GearPredictorCorrectorOrder5(particles,
                new SpringModel(K, GAMMA), STEP, new SpringDerivatives(K, GAMMA));

        //TemporalStepAlgorithmInterface algorithm = new LeapFrog(new SpringModel(10000, 1000), particles);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("output"))){
            for(int t=0; t<5.0/STEP; t++){
                bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                algorithm.step();
            }
        }

    }
}
