package enums;

import java.util.Random;

public enum Subjects {
    ARITHMETIC,
    HISTORY,
    GEOGRAPHY,
    GRAMMAR;

    private static final Random random = new Random();

    public static Subjects getRandomSubject() {
        return values()[random.nextInt(values().length)];
    }
}
