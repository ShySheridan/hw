package Characters;

import Characters.features.Mustache;
import Characters.features.NotEnoughInkException;
import enums.CharacterType;
import enums.Location;

public class LukPorey extends AbstractCharacter {
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
    public void act() {
        System.out.println(name + " is drawing something with mustache.");
    }

    @Override
    public void describe() {
        System.out.println("I am " + name + ", a vegetable!");
        System.out.println("My mustache currently have " + mustache.getInkLevel() + " ink.");

    }
}
