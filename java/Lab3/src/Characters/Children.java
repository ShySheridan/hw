package Characters;

import enums.CharacterType;
import enums.Location;

public class Children extends AbstractCharacter {
    public Children(String name, Location location) {
        super(name, CharacterType.HUMAN, location);
    }

    @Override
    public void act() {
        System.out.println(name + " is playing in the park.");
    }

    @Override
    public void describe() {
        System.out.println("I am " + name + ", a happy child playing around.");
    }
}