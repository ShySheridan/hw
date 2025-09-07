package Lab5.common.src;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
    @XmlElement(required = true)
    private long x;
    @XmlElement(required = true)
    private float y;
    @XmlElement(required = true)
    private double z;

    public Location() { }

    public Location(long x, float y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public long getX() { return x; }
    public float getY() { return y; }
    public double getZ() { return z; }

    public void setX(long x){ this.x = x; }
    public void setY(float y){ this.y = y; }
    public void setZ(double z){ this.z = z; }

}
