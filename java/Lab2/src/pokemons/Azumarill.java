package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.Type;

public final class Azumarill extends Marill {
    public Azumarill(String name, int lvl) {
        super(name, lvl);
        super.setStats(100, 50, 80, 60, 80, 50);
        setType(Type.WATER, Type.FAIRY);
        setMove(new BubbleBeam(), new Scald(), new Supersonic(), new DoubleTeam());

    }
}