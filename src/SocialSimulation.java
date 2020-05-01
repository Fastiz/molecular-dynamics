import algorithms.Beeman;
import forceCalculators.SocialModel;
import models.Particle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SocialSimulation {
    public SocialSimulation() throws IOException{

        double A=2000, B=0.08, vd=0.8, tau=0.5;

        int width=20, height=40;
        SocialModel socialModel = new SocialModel(A, B, vd, tau, height/2.0, 1.2 , width, height);

        List<Particle> particles = generateRandomParticles(0, 0, width, height/2.0 - 1, 0.25, 0.265,200);

        double step = 0.01;

        Beeman beeman = new Beeman(particles, socialModel, step);

        (new File("results")).mkdir();
        (new File("results/social")).mkdir();
        Utils.deleteFiles("results/social/");


        try(BufferedWriter bf = new BufferedWriter(new FileWriter("results/social/static_file"))){
            bf.write(width + " " + height + '\n');
            for(Particle p: particles){
                bf.write(String.valueOf(p.getRadius())+'\n');
            }
        }

        int numberOfIterations = 10000;

        double timePrint = 1/60.0;
        int j=0;
        try(BufferedWriter bf = new BufferedWriter(new FileWriter("results/social/dynamic_file"))) {
            for(int i=0; i<numberOfIterations; i++){
                if(i * step >= j*timePrint){
                    bf.write("#T"+String.valueOf(j)+'\n');
                    for(Particle p : beeman.getParticles()){
                        bf.write(p.getPos().getX() + " " + p.getPos().getY() + "\n");
                    }
                    j++;
                }

                beeman.step();
            }
        }

    }

    private List<Particle> generateRandomParticles(double minX, double minY, double maxX, double maxY, double minRadius, double maxRadius, int numberOfParticles){
        List<Particle> result = new ArrayList<>(numberOfParticles);
        Random rand = new Random();

        int remainingParticles = numberOfParticles;
        while(remainingParticles>0){
            Particle newParticle =  new Particle
                    (
                        rand.nextDouble()*(maxX-minX)+minX,
                        rand.nextDouble()*(maxY-minY)+minY,
                        0,
                        0,
                        rand.nextDouble()*(maxRadius-minRadius)+minRadius,
                        60
                    );
            if(checkCollisions(result, newParticle)){
                result.add(newParticle);
                remainingParticles--;
            }

        }

        return result;
    }

    private boolean checkCollisions(List<Particle> particles , Particle p){
        for(Particle particle : particles){
            if(particle.getPos().distance(p.getPos()) - p.getRadius() - particle.getRadius() <= 0.3)
                return false;
        }
        return true;
    }
}
