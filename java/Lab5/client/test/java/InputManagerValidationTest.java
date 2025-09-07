package Lab5.client.test.java;

import Lab5.client.src.InputManager;
import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.Location;
import Lab5.common.src.enums.Difficulty;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InputManagerValidationTest {

    @Test
    void difficulty_readEnum_skipsGarbage_thenAcceptsValid() {
        // Последовательность ответов:
        // name, coords.x, coords.y, minimalPoint,  [enum Difficulty: "INSANE" (мусор), "HARD" (валид)], author-name (Enter => null)
        String fakeInput = String.join("\n",
                "Work1",          // lab.name
                "1.0",            // coordinates.x (Double)
                "1.0",            // coordinates.y (float > -763)
                "5",              // minimalPoint (float > 0)
                "INSANE",         // Difficulty -> некорректно
                "HARD",           // Difficulty -> валидно
                ""                // author name -> пусто => author = null, и на этом buildLabWork завершится
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fakeInput.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        LabWork lab = im.buildLabWork();

        assertNotNull(lab, "LabWork должен быть создан");
        assertEquals(Difficulty.HARD, lab.getDifficulty(), "Должен принять валидное значение после мусора");
        assertNull(lab.getAuthor(), "Автор должен быть null при пустом вводе имени автора");
    }

    @Test
    void locationX_readLong_skipsNonNumericAndOverflow_thenAcceptsValid() {
        // Дойдём до Person->Location.x, где Long парсится:
        // name, coords.x, coords.y, minimalPoint, difficulty, author.name,
        // author.height, eyeColor, hairColor, nationality,
        // location.x: "abc" (мусор), "99999...999" (переполнение), "42" (валид),
        // location.y, location.z
        String veryBig = "999999999999999999999999999999999999";
        String fakeInput = String.join("\n",
                "Work2",      // lab.name
                "1.0",        // coordinates.x
                "1.0",        // coordinates.y
                "5",          // minimalPoint
                "EASY",       // Difficulty (сразу валидно)
                "Alex",       // author.name (не пусто, чтобы не выйти раньше)
                "180",        // author.height (>0)
                "1",          // eyeColor (индекс из меню enum)
                "2",          // hairColor
                "1",          // nationality
                "abc",        // location.x -> нечисловое
                veryBig,      // location.x -> переполнение Long
                "42",         // location.x -> валидно
                "0.5",        // location.y (float)
                "3.14"        // location.z (double)
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fakeInput.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        LabWork lab = im.buildLabWork();

        assertNotNull(lab, "LabWork должен быть создан");
        assertNotNull(lab.getAuthor(), "Автор должен быть заполнен");
        Person p = lab.getAuthor();
        assertNotNull(p.getLocation(), "Локация автора должна быть заполнена");
        Location loc = p.getLocation();
        assertEquals(42L, loc.getX(), "После мусора и переполнения должен принять валидное значение 42");
    }
    @Test
    void name_blank_thenAcceptsValid_afterReprompt() {
        // name: "" (некорректно) → "WorkX" (валид)
        // дальше минимальный валидный набор, чтобы собрать LabWork и завершить ввод
        String fake = String.join("\n",
                "",             // name -> некорректно, повторный запрос
                "WorkX",        // name -> валид
                "0",            // coordinates.x
                "0",            // coordinates.y (> -763)
                "1",            // minimalPoint (> 0)
                "EASY",         // difficulty
                ""              // author.name -> пусто => author = null
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        var lab = im.buildLabWork();

        assertNotNull(lab);
        assertEquals("WorkX", lab.getName());
        assertNull(lab.getAuthor(), "При пустом вводе имени автора автор должен быть null");
    }

    @Test
    void coordinatesY_strictLowerBound_invalidAtMinus763_thenAcceptsAbove() {
        // y: -763 (некорректно, т.к. требуется > -763) → -762.5 (валид)
        // difficulty: используем валидное значение из доступных (1..3 или EASY/HARD/IMPOSSIBLE),
        // чтобы тест не завис на выборе enum и не словил EOF.
        String fake = String.join("\n",
                "WorkY",     // name
                "0",         // coordinates.x
                "-763",      // coordinates.y -> некорректно
                "-762.5",    // coordinates.y -> валид
                "1",         // minimalPoint (> 0)
                "2",         // Difficulty -> "HARD" по меню 1)EASY 2)HARD 3)IMPOSSIBLE
                ""           // author.name -> пусто => author = null
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        var lab = im.buildLabWork();

        assertNotNull(lab);
        assertEquals(-762.5f, lab.getCoordinates().getY(), 1e-6f,
                "Должно принять значение строго больше -763");
    }


    @Test
    void minimalPoint_strictLowerBound_zeroRejected_thenPositiveAccepted() {
        // minimalPoint: 0 (некорректно, т.к. > 0) → 0.01 (валид)
        String fake = String.join("\n",
                "WorkMin",      // name
                "1.0",          // coordinates.x
                "1.0",          // coordinates.y
                "0",            // minimalPoint -> некорректно
                "0.01",         // minimalPoint -> валид
                "HARD",         // difficulty
                ""              // author.name
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        var lab = im.buildLabWork();

        assertNotNull(lab);
        assertTrue(lab.getMinimalPoint() > 0.0f, "Значение должно быть строго > 0");
    }

    @Test
    void authorHeight_zeroRejected_thenPositiveAccepted() {
        // Проверим граничное условие для роста автора (> 0):
        // высота: "0" (некорректно) → "170" (валид)
        // Для завершения — минимальные валидные значения для остальных полей автора
        String fake = String.join("\n",
                "WorkP",    // name
                "0",        // coordinates.x
                "0",        // coordinates.y
                "1",        // minimalPoint
                "EASY",     // difficulty
                "Alex",     // author.name (не пусто, чтобы пройти блок автора)
                "0",        // author.height -> некорректно
                "170",      // author.height -> валид
                "1",        // eyeColor (из меню)
                "1",        // hairColor (из меню)
                "1",        // nationality (из меню)
                "1",        // location.x (long)
                "0.5",      // location.y (float)
                "3.14"      // location.z (double)
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        var lab = im.buildLabWork();

        assertNotNull(lab);
        assertNotNull(lab.getAuthor());
        assertTrue(lab.getAuthor().getHeight() > 0, "Рост автора должен быть строго > 0");
    }

    @Test
    void enumIndex_outOfRange_thenAcceptsValidIndex() {
        // Проверим выбор enum по индексу: вне диапазона -> повтор, затем валидный индекс
        // (Если у тебя выбор по имени, этот тест всё равно отработает как "мусор" → "валид")
        String fake = String.join("\n",
                "WorkEnum", // name
                "0",        // coordinates.x
                "0",        // coordinates.y
                "1",        // minimalPoint
                "EASY",     // difficulty (валид сразу, чтоб быстрее)
                "Bob",      // author.name
                "180",      // author.height
                "0",        // eyeColor -> некорректный индекс (ожидается 1..N)
                "1",        // eyeColor -> валид
                "99",       // hairColor -> некорректный индекс
                "1",        // hairColor -> валид
                "-1",       // nationality -> некорректный индекс
                "1",        // nationality -> валид
                "10",       // location.x
                "0.0",      // location.y
                "0.0"       // location.z
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        InputManager im = new InputManager(sc);

        var lab = im.buildLabWork();

        assertNotNull(lab);
        assertNotNull(lab.getAuthor());
        assertNotNull(lab.getAuthor().getLocation());
    }
}
