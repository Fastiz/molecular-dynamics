package interfaces;

import models.Force;
import models.Particle;

import java.util.List;

public interface ForcesCalculator {
    public List<Particle> calculate(List<Particle> particleList);
}
