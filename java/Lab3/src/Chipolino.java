import Characters.*;
import Characters.features.NoSeedsException;
import Characters.features.NotEnoughInkException;
import Services.Castle;
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
        Ruler ruler_Tomato = new Ruler("Signore Tomato", CharacterType.VEGETABLE, Location.CASTLE);
        SignoreTomato signoreTomato = new SignoreTomato(9);

        Children child1 = new Children("Alice", Location.NONE);
        Children child2 = new Children("Bob", Location.NONE);
        ArrayList<Children> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);

        Park park = new Park();

        characters.add(lukPorey);
        characters.add(princeLemon);
        characters.add(ruler_Tomato);
        characters.addAll(children);
        characters.add(signoreTomato);

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
                if (character instanceof SignoreTomato){
                    signoreTomato.plantSeeds();
                    signoreTomato.trimGrass();
                }
            } catch (NotEnoughInkException e) {
                System.out.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            } catch (NoSeedsException e) {
                throw new RuntimeException(e);
            }
        }

        park.updateStatusBasedOnRulers();
        Castle castle = new Castle();

        if (park.isOpen()) {
            for (Children child : children) {
                child.setLocation(Location.PARK);
                child.act();
            }
        }else{
            castle.school(child1, child2);
            castle.pingPong(child1, child2);
            castle.cinema(child1,child2);
            castle.theater(child1);
            }
    }
}
