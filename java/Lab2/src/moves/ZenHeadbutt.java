package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class ZenHeadbutt extends PhysicalMove {
    boolean flinch = false;
    public ZenHeadbutt(){
        super(Type.PSYCHIC, 80, 90);}

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (Math.random() <= 0.2){
            Effect.flinch(pokemon);
            flinch = true;
        }
    }
    @Override
    protected String describe(){
        return "is using Zen Headbutt";}
}
