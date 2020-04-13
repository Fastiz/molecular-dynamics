package models;

import java.util.ArrayList;
import java.util.List;

public class Particle {
    private double radius;
    private double mass;
    private List<Vector> derivatives;

    public Particle(List<Vector> derivatives) {
        this.derivatives = derivatives;
    }

    public Particle(Vector pos, Vector vel){
        this.derivatives = new ArrayList<>();

        this.derivatives.add(pos);
        this.derivatives.add(vel);
    }

    public Particle(Vector pos, Vector vel, double radius, double mass){
        this.derivatives = new ArrayList<>();

        this.derivatives.add(pos);
        this.derivatives.add(vel);

        this.radius = radius;
        this.mass = mass;
    }

    public Particle(double x, double y, double vx, double vy, double radius, double mass){
        this.derivatives = new ArrayList<>();

        this.derivatives.add(new Vector(x, y));
        this.derivatives.add(new Vector(vx, vy));

        this.radius = radius;
        this.mass = mass;
    }

    public Vector getPos(){
        return this.derivatives.get(0);
    }

    public Vector getVel(){
        return this.derivatives.get(1);
    }

    public Vector getAcel(){
        return this.derivatives.get(2);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setPosition(double x, double y){
        this.derivatives.set(0, new Vector(x, y));
    }

    public double getMass(){
        return mass;
    }

    public void setVel(double x, double y){
        this.derivatives.set(1, new Vector(x, y));
    }

    public List<Vector> getDerivatives() {
        return derivatives;
    }
}
