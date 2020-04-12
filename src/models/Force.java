package models;

public class Force {
    private double x, y;
    private double[][] derivatives;

    public Force(double x, double y){
        this.x = x;
        this.y = y;
        this.derivatives = null;
    }

    public Force(double[][] derivatives){
        this.x = derivatives[0][0];
        this.y = derivatives[0][1];
        this.derivatives = derivatives;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double[][] getDerivatives() {
        return derivatives;
    }
}
