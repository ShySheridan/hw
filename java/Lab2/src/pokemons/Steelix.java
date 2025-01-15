package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.Type;

public final class Steelix extends Onix {
    public Steelix(String name, int lvl) {
        super(name, lvl);
        super.setStats(75, 85, 200, 55, 65, 30);
        setType(Type.STEEL, Type.GROUND);
        setMove(new RockTomb(), new Facade(), new RockPolish(), new FireFang());

    }
}
