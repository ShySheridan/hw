package pokemons;

import moves.*;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Onix extends Pokemon {
    public Onix(String name, int lvl) {
        super(name, lvl);
        super.setStats(35, 45, 160, 30, 45, 70);
        setType(Type.ROCK, Type.GROUND);
        setMove(new RockTomb(), new Facade(), new RockPolish());
    }
    }

