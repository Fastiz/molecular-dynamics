package interfaces;

import models.Particle;

import java.util.List;

public interface DerivativesCalculator {
    public List<Particle> calculate(List<Particle> particles);
}
