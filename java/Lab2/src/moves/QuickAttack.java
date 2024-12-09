package moves;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;

public class QuickAttack extends PhysicalMove {
    public QuickAttack() {
        super(Type.NORMAL, 100, 40, 1, 1);}

    @Override protected String describe(){
        return "is using Quick Attack";}
}
