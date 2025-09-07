package Lab3.src.Characters;

import Characters.features.Mustache;
import Characters.features.NotEnoughInkException;
import enums.CharacterType;
import enums.Location;

import java.util.Objects;

public class LukPorey extends Characters.AbstractCharacter {
    private final Mustache mustache;

    public LukPorey(int inkLevel, Location location) {
        super("Luk Porey", CharacterType.VEGETABLE, location.VILLAGE);
        this.mustache = new Mustache(inkLevel);
    }

    public void draw() throws NotEnoughInkException {
        if (!mustache.hasInk()) {
            throw new NotEnoughInkException("Mustache has no ink available to write!");
        }
        mustache.dipInInk();
        System.out.println(name + " is writing with the mustache.");
    }

    @Override
    public void describe() {
        System.out.println("I am " + name + ", a vegetable!");
        System.out.println("My mustache currently have " + mustache.getInkLevel() + " ink.");
    }

    @Override
    public void act() {
        System.out.println(name + " is screaming hysterically 'Текили-ли!'");
    }

    @Override
    public String toString(){
        return String.format("Character{name='%s', type=%s, location=%s}", name, type, location);
    }

    @Override
    public  boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Characters.AbstractCharacter character = (Characters.AbstractCharacter) o;
        return Objects.equals(name, character.name) && type == character.type;
    }

    @Override
    public  int hashCode() {
        return Objects.hash(name, type);
    }
}
