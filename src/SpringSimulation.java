import algorithms.Beeman;
import algorithms.GearPredictorCorrectorOrder5;
import algorithms.LeapFrog;
import forceCalculators.SpringDerivatives;
import forceCalculators.SpringModel;
import interfaces.TemporalStepAlgorithmInterface;
import models.Particle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpringSimulation {
    public SpringSimulation() throws IOException {
        double K = 10000, GAMMA = 100, MASS = 70;

        int NUMBER_OF_ITERATIONS = 7;
        double step = 1.0/Math.pow(10, NUMBER_OF_ITERATIONS);

        (new File("results")).mkdir();

        (new File("results/spring")).mkdir();

        (new File("results/spring/gear_predictor_corrector/")).mkdir();

        (new File("results/spring/leap_frog/")).mkdir();

        (new File("results/spring/beeman/")).mkdir();

        Utils.deleteFiles("results/spring/gear_predictor_corrector/");
        Utils.deleteFiles("results/spring/leap_frog/");
        Utils.deleteFiles("results/spring/beeman/");

        for(int i = 2 ; i<NUMBER_OF_ITERATIONS; i++){
            step*=10;
            System.out.println("On iteration: " + i + ". With step: "+ step);

            List<Particle> particles = new ArrayList<>();
            particles.add(new Particle(1, 0, -GAMMA/(2*MASS), 0, 1, MASS));

            TemporalStepAlgorithmInterface algorithm;

            algorithm = new GearPredictorCorrectorOrder5(particles,
                    new SpringModel(K, GAMMA), step, new SpringDerivatives(K, GAMMA));
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("results/spring/gear_predictor_corrector/sim"+i))){
                bw.write(step+"\n");
                for(int t=0; t<5.0/step; t++){
                    bw.write(algorithm.getParticles().get(0).getPos().getX() + " " + algorithm.getParticles().get(0).getPos().getY() + "\n");
                    algorithm.step();
                }
            }

            algorithm = new LeapFrog(particles, new SpringModel(K, GAMMA), step);
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("results/spring/leap_frog/sim"+i))){
                bw.write(step+"\n");
                for(int t=0; t<5.0/step; t++){
                    bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                    algorithm.step();
                }
            }

            algorithm = new Beeman(particles, new SpringModel(K, GAMMA), step);
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("results/spring/beeman/sim"+i))){
                bw.write(step+"\n");
                for(int t=0; t<5.0/step; t++){
                    bw.write(algorithm.getParticles().get(0).getPos().getX() + "\n");
                    algorithm.step();
                }
            }
        }

    }
}
