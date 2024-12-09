package moves;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class Supersonic extends StatusMove {
    public Supersonic(){
        super(Type.NORMAL, 0, 55);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        Effect.confuse(p);}
    @Override
    protected String describe(){
        return "is using Sypersonic";
    }
}
