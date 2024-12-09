package moves;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;

public class XScissors extends PhysicalMove {
    public XScissors() {
        super(Type.BUG, 100, 80);
    }

    @Override
    protected String describe(){
        return "is using Horn Leech";
    }
}
