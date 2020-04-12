package models;

public class Particle {
    private Vector pos;
    private Vector vel;
    private double radius;
    private double mass;

    public Particle() {

    }

    public Particle(Vector pos, Vector vel, double radius, double mass){
        this.pos = pos;
        this.radius = radius;
        this.vel = vel;
        this.mass = mass;
    }

    public Particle(double x, double y, double vx, double vy, double radius, double mass){
        this.pos = new Vector(x, y);
        this.radius = radius;
        this.vel = new Vector(vx, vy);
        this.mass = mass;
    }

    public Vector getPos(){
        return pos;
    }

    public Vector getVel(){
        return vel;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setPosition(double x, double y){
        this.pos = new Vector(x, y);
    }

    public double getMass(){
        return mass;
    }

    public void setVel(double x, double y){
        this.vel = new Vector(x, y);
    }
}
