package pokemons;

import moves.BubbleBeam;
import moves.Scald;
import moves.Supersonic;
import ru.ifmo.se.pokemon.Type;

public class Marill extends Azurill {
    public Marill(String name, int lvl) {
        super(name, lvl);
        super.setStats(70, 20, 50, 20, 50, 40);
        setType(Type.WATER, Type.FAIRY);
        setMove(new BubbleBeam(), new Scald(), new Supersonic());

    }
}
