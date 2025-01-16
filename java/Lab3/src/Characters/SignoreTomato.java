package Characters;

import Characters.features.CabbageSeeds;
import Characters.features.NoSeedsException;
import enums.CharacterType;
import enums.Location;

import java.util.Objects;
import java.util.Random;

public class SignoreTomato extends AbstractCharacter {

    private CabbageSeeds seeds;
    private boolean grassTrimmed;

    public SignoreTomato(int totalSeeds) {
        super("Signore Tomato", CharacterType.VEGETABLE, Location.GARBAGE);
        if (totalSeeds < 0) {
            throw new IllegalArgumentException("Seed count cannot be less than zero");
        }
        this.seeds = new CabbageSeeds(totalSeeds, 0);
        this.grassTrimmed = false;
    }

    public void plantSeeds() throws NoSeedsException {
        if (seeds.totalSeeds() <= 0) {
            throw new NoSeedsException(name + " has no seeds left to plant!");
        }
        Random random = new Random();

        int seedsToPlant = random.nextInt(seeds.totalSeeds() + 1); // Случайное число посаженных семян
        int sprouted = random.nextInt(seedsToPlant + 1); // Случайное число проросших из посаженных

        // Обновляем статистику семян
        seeds = new CabbageSeeds(seeds.totalSeeds() - seedsToPlant,
                seeds.sproutedSeeds() + sprouted);

        System.out.printf("%s planted %d seeds, %d sprouted. Remaining seeds: %d%n",
                name, seedsToPlant, sprouted, seeds.totalSeeds());
    }

    public void trimGrass() {
        if (grassTrimmed) {
            System.out.println(name + ": The grass is already trimmed.");
        } else {
            grassTrimmed = true;
            System.out.println(name + " trimmed the grass.");
        }
    }

    @Override
    public void act() {
        System.out.printf("%s is managing the garden in %s.%n", name, location);
    }

    @Override
    public void describe() {
        System.out.println("I am " + name + ", a vegetable and now I am free!");
    }

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

