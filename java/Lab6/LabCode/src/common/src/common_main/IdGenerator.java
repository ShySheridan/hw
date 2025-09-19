package common_main;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class IdGenerator {
    private static final Set<Long> usedIds = new HashSet<>();
    private static final Random random = new Random();

    public static long generateUniqueId() {
        long id;
        do {
            id = Math.abs(random.nextLong());
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

}

