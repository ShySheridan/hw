import pokemons.Absol;
import pokemons.Azumarill;
import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

public class Main {

    public static void main(String[] args){
        Battle b = new Battle();
        Pokemon p1 = new Pokemon("Чужой", 1);
        Pokemon p2 = new Pokemon("Хищник", 1);
        Absol a = new Absol("Absol", 1);
        Azumarill gghv = new Azumarill("Azumarill", 1);
        b.addAlly(gghv);
        b.addFoe(a);
        b.go();
    }
}
