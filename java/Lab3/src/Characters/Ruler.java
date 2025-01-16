package Characters;

import enums.CharacterType;
import enums.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ruler extends AbstractCharacter {
    private static final List<Ruler> rulers = new ArrayList<>();

    public Ruler(String name, CharacterType type, Location location) {
        super(name, type, location);
        rulers.add(this);
    }

    public void expel() {
        this.setLocation(Location.NONE);
        System.out.println(name + " has been kicked out of the castle!");
    }

    public static boolean areAllOutOfCastle() {
        return rulers.stream().allMatch(ruler -> ruler.getLocation() != Location.CASTLE);
    }

    @Override
    public void act() {
        System.out.println(name + " rules the castle");
    }

    @Override
    public void describe() {
        System.out.println("I am " + name );
    }

    @Override
    public String toString(){
        return String.format("Character{name='%s', type=%s, location=%s}", name, type, location);

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
