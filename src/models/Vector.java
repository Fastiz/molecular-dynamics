package models;

public class Vector {
    private double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void sum(Vector v){
        this.x += v.x;
        this.y += v.y;
    }

    public static Vector scalarMultiplicationVector(double scalar, Vector c){
        return new Vector(c.x*scalar, c.y*scalar);
    }

    @Override
    public String toString() {
        return "(" + this.x +", " + this.y + ")";
    }
}
