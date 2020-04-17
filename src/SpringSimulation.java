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

public class SpringSimulation {
    public SpringSimulation() throws IOException {
        double K = 10000, GAMMA = 100, MASS = 70, STEP=0.001;

        List<Particle> particles = new ArrayList<>();
        particles.add(new Particle(1, 0, -GAMMA/(2*MASS), 0, 1, MASS));

        TemporalStepAlgorithmInterface algorithm;

        algorithm = new GearPredictorCorrectorOrder5(particles,
                new SpringModel(K, GAMMA), STEP, new SpringDerivatives(K, GAMMA));
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("spring_gear_predictor_corrector"))){
            for(int t=0; t<5.0/STEP; t++){
                bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                algorithm.step();
            }
        }

        algorithm = new LeapFrog(particles, new SpringModel(K, GAMMA), STEP);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("spring_leap_frog"))){
            for(int t=0; t<5.0/STEP; t++){
                bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                algorithm.step();
            }
        }

        algorithm = new Beeman(particles, new SpringModel(K, GAMMA), STEP);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("spring_beeman"))){
            for(int t=0; t<5.0/STEP; t++){
                bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                algorithm.step();
            }
        }
    }
}
