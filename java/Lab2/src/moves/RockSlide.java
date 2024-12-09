package moves;

import ru.ifmo.se.pokemon.*;

public class RockSlide extends PhysicalMove {
    boolean flinch = false;
    public RockSlide(){
        super(Type.ROCK, 75, 90);
    }
    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (Math.random() <= 0.3){
            Effect.flinch(pokemon);
            flinch = true;
        }
    }

    @Override
    protected String describe() {
        return "is using Rock Slide";
    }
}
