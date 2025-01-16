package Characters;

import enums.CharacterType;
import enums.Location;

import java.util.Objects;

public class Children extends AbstractCharacter {
    public Children(String name, Location location) {
        super(name, CharacterType.HUMAN, location);
    }

    public String getName(){
        return name;
    }

    @Override
    public void act() {
        System.out.println(name + " is playing in the park.");
    }

    @Override
    public String toString(){
        return String.format("Character{name='%s', type=%s, location=%s}", name, type, location);

    }
    @Override
    public void describe() {
        System.out.println("I am " + name + ", a happy child playing around.");
    }

    @Override
    public  boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCharacter character = (AbstractCharacter) o;
        return Objects.equals(name, character.name) && type == character.type;
    }

    @Override
    public  int hashCode() {
        return Objects.hash(name, type);
    }
}