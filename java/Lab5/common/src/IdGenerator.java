package Lab5.common.src;

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

    //      загружает уже использованные id
    public static void preloadUsedIds(Set<Long> existingIds) {
        usedIds.addAll(existingIds);
    }

//    очистка, если нужно перезапустить генератор
    public static void reset() {
        usedIds.clear();
    }
}

//TODO в конце программы нужно сбрасывать usedIds,
// куда запихунть preloadUserIds(), reset()
//
