package common_main;

import java.io.Serializable;

public class Coordinates implements Serializable {

    private Double x; // not null
    private float y;

    public Coordinates() {}

    public Coordinates(Double x, float y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    @Override
    public String toString() {
        return "main.Coordinates{x=" + x + ", y=" + y + '}';
    }
}
