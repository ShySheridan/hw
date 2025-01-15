package Characters;

import enums.CharacterType;
import enums.Location;

import java.util.ArrayList;
import java.util.List;

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
}
