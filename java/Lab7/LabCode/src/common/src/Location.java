package src.common.src;

import java.io.Serializable;

public class Location implements Serializable {

    private double x;
    private float y;
    private double z;

    public Location() { }

    public Location(double x, float y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double getX() { return x; }
    public float getY() { return y; }
    public double getZ() { return z; }

    public void setX(double x){ this.x = x; }
    public void setY(float y){ this.y = y; }
    public void setZ(double z){ this.z = z; }

}
