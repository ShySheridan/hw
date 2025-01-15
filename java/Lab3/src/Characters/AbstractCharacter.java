package Characters;

import Interfaces.Describable;
import enums.CharacterType;
import enums.Location;

import java.util.Objects;

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

    @Override
    public String toString() {
        return String.format("Character{name='%s', type=%s, location=%s}", name, type, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCharacter character = (AbstractCharacter) o;
        return Objects.equals(name, character.name) && type == character.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
