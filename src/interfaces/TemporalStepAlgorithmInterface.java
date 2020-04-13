package interfaces;

import models.Particle;

import java.util.List;

public interface TemporalStepAlgorithmInterface {
    public void step();

    public List<Particle> getParticles();
}
