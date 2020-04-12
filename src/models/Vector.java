package models;

public class Vector {
    private double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double SquaredDistanceFrom(Vector otherPos){
        return SquaredDistanceBetween(this, otherPos);
    }

    public static double SquaredDistanceBetween(Vector pos1, Vector pos2){
        return (double)(Math.pow(pos1.x-pos2.x, 2) + Math.pow(pos1.y-pos2.y, 2));
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

    @Override
    public String toString() {
        return "(" + this.x +", " + this.y + ")";
    }
}
