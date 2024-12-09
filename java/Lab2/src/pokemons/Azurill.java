package pokemons;

import moves.QuickAttack;
import moves.RockSlide;
import moves.XScissors;
import moves.ZenHeadbutt;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Azurill extends Pokemon {
    public Azurill(String name, int lvl) {
        super(name, lvl);
        super.setStats(65, 130, 60, 75, 75, 60);
        setType(Type.DARK);
        setMove(new QuickAttack(), new ZenHeadbutt(), new XScissors(), new RockSlide());

    }
}