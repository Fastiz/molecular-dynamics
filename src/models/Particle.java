package models;

public class Particle {
    private Vector pos;
    private Vector vel;
    private double radius;
    private double mass;
    private int id;

    public static final Particle LEFT_WALL = new Particle(),
            RIGHT_WALL = new Particle(),
            TOP_WALL = new Particle(),
            BOTTOM_WALL = new Particle();

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

    public boolean IsInsideActionRadiusOf(Particle otherParticle, double actionRadius){
        double squareActionRadiusPlusRadius = (double) Math.pow(actionRadius + radius + otherParticle.radius, 2);
        return squareActionRadiusPlusRadius >= otherParticle.pos.SquaredDistanceFrom(pos);
    }

    public Vector getPos(){
        return pos;
    }

    public Vector getVel(){
        return vel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
