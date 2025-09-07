package Lab5.server.test.java;// CommandParameterValidationTest.java
// пакет пропусти или поставь свой, если используешь package-структуру

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

// Импорты под твои пакеты (если у тебя другие — поправь здесь):
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;
import Lab5.server.src.commands.Command;
import Lab5.server.src.commands.RemoveByID;

public class CommandParameterValidationTest {

    @Test
    void removeById_rejectsNonNumeric() {
        Command cmd = new RemoveByID(new CollectionManager());

        // Неверный тип: буквы вместо числа
        Request req = new Request("remove_by_id", "abc");

        Response resp = cmd.execute(req);

        assertFalse(resp.isSuccess(), "Должно быть неуспешно при нечисловом id");
        assertTrue(resp.getMessage().toLowerCase().contains("id"),
                "Сообщение должно указывать на проблему с id");
    }

    @Test
    void removeById_rejectsOverflow() {
        Command cmd = new RemoveByID(new CollectionManager());

        // Слишком большое число -> NumberFormatException внутри команды
        String veryBig = "9999999999999999999999999999999999999";
        Request req = new Request("remove_by_id", veryBig);

        Response resp = cmd.execute(req);

        assertFalse(resp.isSuccess(), "Должно быть неуспешно при переполнении id");
        assertTrue(resp.getMessage().toLowerCase().contains("id"),
                "Сообщение должно подсказывать проблему с числом id");
    }

    @Test
    void removeById_acceptsValidNumericString() {
        Command cmd = new RemoveByID(new CollectionManager());
        Request req = new Request("remove_by_id", "42"); // <-- строка

        Response resp = cmd.execute(req);

        String msg = resp.getMessage() == null ? "" : resp.getMessage().toLowerCase();
        assertFalse(
                msg.contains("формат") ||
                        msg.contains("format") ||
                        msg.contains("некоррект") ||
                        msg.contains("числ") ||
                        msg.contains("переполн"),
                "Для валидной числовой строки не должно быть ошибок формата id"
        );
    }

    @Test
    void removeById_rejectsZero() {
        Command cmd = new RemoveByID(new CollectionManager());
        Request req = new Request("remove_by_id", 0L); // граничное: 0

        Response resp = cmd.execute(req);

        assertFalse(resp.isSuccess(), "id = 0 должен отвергаться");
        assertTrue(resp.getMessage().toLowerCase().contains("id")
                        || resp.getMessage().toLowerCase().contains("0"),
                "Ожидается понятное сообщение про недопустимый id");
    }

    @Test
    void removeById_rejectsNegative() {
        Command cmd = new RemoveByID(new CollectionManager());
        Request req = new Request("remove_by_id", -1L); // граничное: отрицательное

        Response resp = cmd.execute(req);

        assertFalse(resp.isSuccess(), "Отрицательный id должен отвергаться");
        assertTrue(resp.getMessage().toLowerCase().contains("id")
                        || resp.getMessage().toLowerCase().contains("> 0"),
                "Ожидается подсказка, что id должен быть > 0");
    }

    @Test
    void removeById_acceptsLongMax_withoutFormatError() {
        Command cmd = new RemoveByID(new CollectionManager());

        // Передаём Long.MAX_VALUE строкой — 19 цифр, без пробелов и знаков:
        String longMaxStr = String.valueOf(Long.MAX_VALUE); // "9223372036854775807"
        Request req = new Request("remove_by_id", longMaxStr);

        Response resp = cmd.execute(req);

        String msg = resp.getMessage() == null ? "" : resp.getMessage().toLowerCase();

        // Допустимо "не найден" — важно лишь, что НЕТ жалобы на формат/переполнение
        assertFalse(
                msg.contains("формат") ||
                        msg.contains("format") ||
                        msg.contains("некоррект") ||
                        msg.contains("числ") ||
                        msg.contains("переполн"),
                "Для Long.MAX_VALUE (строкой) не должно ругаться на формат/переполнение"
        );
    }


}
