package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class FireFang extends PhysicalMove {
    boolean flinch = false;
    public FireFang(){
        super(Type.FIRE, 65, 95);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        if (Math.random() < 0.1){
            Effect.burn(p);
        }
        if (Math.random() < 0.1){
            Effect.flinch(p);
            flinch = true;
        }
    }
    @Override
    protected String describe(){
        return "is using Fire Fang";
    }
}
