package Characters.features;

import java.util.Objects;

public class Mustache {
    private int inkLevel;
    private boolean dipped;

    public Mustache(int inkLevel) {
        if (inkLevel <0){
            throw new IllegalArgumentException("ink level can`t be less that zero");
        }
        this.inkLevel = inkLevel;
        this.dipped = false;
    }

    public boolean hasInk() {
        return inkLevel > 0;
    }

    public void dipInInk() {
        if (inkLevel <= 0) {
            throw new IllegalStateException("Not enough ink to dip!");
        }
        dipped = true;
        inkLevel--;
    }

    public int getInkLevel() {
        return inkLevel;
    }

    @Override
    public String toString() {
        return String.format("Mustache{inkLevel=%d, dipped=%b}", inkLevel, dipped);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mustache mustache = (Mustache) o;
        return inkLevel == mustache.inkLevel && dipped == mustache.dipped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inkLevel, dipped);
    }
}
