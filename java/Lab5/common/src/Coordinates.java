package Lab5.common.src;

import jakarta.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @XmlElement(required = true)
    private Double x;
    @XmlElement(required = true)
    private float y;

    public Coordinates() {}

    public Coordinates(Double x, float y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        if (x == null) throw new IllegalArgumentException("X cannot be null");
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (y <= -763) throw new IllegalArgumentException("Y must be greater than -763");
        this.y = y;
    }

    @Override
    public String toString() {
        return "x= " + x + ", y= " + y ;
    }
}
