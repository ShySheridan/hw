package Characters;

import Characters.features.CabbageSeeds;
import enums.CharacterType;
import enums.Location;

public class SignoreTomato extends AbstractCharacter{
    private final CabbageSeeds seeds;

    public SignoreTomato(int seeds, Location location){
        super("Signore Tomato", CharacterType.VEGETABLE, location.GARBAGE);
        this.seeds = new CabbageSeeds(unripeSeeds);
    }
}

