import pokemons.*;
import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

public class Main {

    public static void main(String[] args){
        Battle b = new Battle();
        Pokemon p1 = new Pokemon("Чужой", 1);
        Pokemon p2 = new Pokemon("Хищник", 1);
        Absol a1 = new Absol("Absol", 1);
        Azumarill a2 = new Azumarill("Azumarill", 1);
        Marill a3 = new Marill("Marill", 1);
        Steelix a5 = new Steelix("Steelix", 1);
        Onix a4 = new Onix("Onix", 1);
        Azurill a6 = new Azurill("Azurill", 1);

        b.addAlly(a1);
        b.addFoe(a2);
        b.addAlly(a3);
        b.addFoe(a4);
        b.addAlly(a5);
        b.addFoe(a6);

        b.go();
    }
}
