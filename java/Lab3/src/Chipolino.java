import Characters.AbstractCharacter;
import Characters.Children;
import Characters.LukPorey;
import Characters.Ruler;
import Characters.features.NotEnoughInkException;
import Services.Park;
import enums.CharacterType;
import enums.Location;

import java.util.ArrayList;
import java.util.Random;

public class Chipolino {
    public static void main(String[] args) {
        ArrayList<AbstractCharacter> characters = new ArrayList<>();
        LukPorey lukPorey = new LukPorey(10, Location.VILLAGE);
        Ruler princeLemon = new Ruler("Prince Lemon", CharacterType.FRUIT, Location.CASTLE);
        Ruler seniorTomato = new Ruler("Seinor Tomato", CharacterType.VEGETABLE, Location.CASTLE);

        Children child1 = new Children("Alice", Location.NONE);
        Children child2 = new Children("Bob", Location.NONE);
        ArrayList<Children> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        Park park = new Park();

        characters.add(lukPorey);
        characters.add(princeLemon);
        characters.add(seniorTomato);
        characters.addAll(children);

        Random random = new Random();

        for (AbstractCharacter character : characters) {
            character.describe();
            try {
                character.act();
                if (character instanceof LukPorey) {
                    lukPorey.draw();
                }
                if (character instanceof Ruler && random.nextBoolean()) {
                    ((Ruler) character).expel();
                }
            } catch (NotEnoughInkException e) {
                System.out.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }

        park.updateStatusBasedOnRulers();

        if (park.isOpen()) {
            for (Children child : children) {
                child.setLocation(Location.PARK);
                child.act();
            }
        }
    }
}
