package Lab5.common.test.java;

import Lab5.common.src.Coordinates;
import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.enums.Difficulty;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет, что модель/создание LabWork не пропускает некорректные значения.
 */
public class LabWorkConstraintsTest {

    @Test
    void name_mustNotBeNullOrEmpty() {
        LabWork lw = new LabWork();
        assertThrows(IllegalArgumentException.class, () -> lw.setName(null));
        assertThrows(IllegalArgumentException.class, () -> lw.setName(""));
    }

    @Test
    void coordinates_rejectTooBigValues() {
        Coordinates c = new Coordinates();
        // Подставь реальные ограничения, если они есть в ТЗ:
        assertThrows(IllegalArgumentException.class, () -> c.setX(Double.POSITIVE_INFINITY)); //:TODO: check
        assertThrows(IllegalArgumentException.class, () -> c.setY(Float.NaN));
    }

    @Test
    void difficulty_rejectsInvalidEnumString() {
        // эмуляция парсинга enum из строки
        assertThrows(IllegalArgumentException.class, () -> Difficulty.valueOf("SUPER_HARD"));
    }
    @Test
    void coordinatesY_boundary_check() {
        Coordinates c = new Coordinates();
        assertThrows(IllegalArgumentException.class, () -> c.setY(-763.0f),
                "y == -763 должно отвергаться при строгом > -763");
        c.setY(-762.99994f); // валидная граница
        assertTrue(c.getY() > -763.0f);
    }

    @Test
    void minimalPoint_boundary_check() {
        LabWork lw = new LabWork();
        assertThrows(IllegalArgumentException.class, () -> lw.setMinimalPoint(0.0f),
                "0.0f должно отвергаться при строгом > 0");
        lw.setMinimalPoint(0.00001f);
        assertTrue(lw.getMinimalPoint() > 0.0f);
    }

    @Test
    void height_boundary_check() {
        Person p = new Person();
        assertThrows(IllegalArgumentException.class, () -> p.setHeight(0),
                "Рост 0 должен отвергаться");
        p.setHeight(1);
        assertEquals(1, p.getHeight());
    }

    @Test
    void name_trimmed_notEmpty() {
        LabWork lw = new LabWork();
        assertThrows(IllegalArgumentException.class, () -> lw.setName("   "),
                "Пробелы должны считаться пустым именем");
        lw.setName(" A ");
        assertEquals(" A ", lw.getName(), "Если тримминг не обязателен в модели — значение сохраняется как есть");
    }

}
