package Characters;

import Interfaces.Describable;
import enums.CharacterType;
import enums.Location;

public abstract class AbstractCharacter implements Describable {
    protected String name;
    protected CharacterType type;
    protected Location location;

    public AbstractCharacter(String name, CharacterType type, Location location) {
        this.location = location;
        this.name = name;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public abstract void act();

    public abstract String toString();

    public abstract boolean equals(Object o);

    public abstract int hashCode();

}
