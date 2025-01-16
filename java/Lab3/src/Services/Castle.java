package Services;

import Characters.Children;
import enums.Subjects;
import java.util.Random;

public class Castle {
    private final Random random = new Random();

    public void theater(Children child1) {
        System.out.println(child1.getName() + " is watching a play with Pinocchio");
    }

    public void cinema(Children child1, Children child2){
        System.out.println(child1.getName() + " and " + child2.getName() + " are watching a movie");
    }
    public void school(Children child1, Children child2) {
        Subjects subject = Subjects.getRandomSubject();
        System.out.println( child1.getName() + child2.getName() + " are learning " + subject);
    }

    public void pingPong(Children child1, Children child2) {
        String winner = random.nextBoolean() ? child1.getName() : child2.getName();
        System.out.printf("%s and %s are playing ping-pong. The winner is %s!%n",
                child1.getName(), child2.getName(), winner);
    }
}
