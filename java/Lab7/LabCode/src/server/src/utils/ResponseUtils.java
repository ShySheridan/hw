package src.server.src.utils;

import src.common.src.Coordinates;
import src.common.src.LabWork;
import src.common.src.Location;
import src.common.src.Person;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Утилиты форматирования текстовых ответов для команд сервера.
 * Единый «глубокий» вывод LabWork и вспомогательные сборщики текста.
 */
public final class ResponseUtils {
    private ResponseUtils() {
    }

    private static final String SEP = System.lineSeparator() + System.lineSeparator() + "----------------------------------------" + System.lineSeparator() + System.lineSeparator();
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    /**
     * Полное форматирование LabWork со всеми вложенными объектами.
     */
    public static String formatLabWorkDeep(LabWork lw) {
        if (lw == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("LabWork {\n");
        sb.append("  id: ").append(lw.getId()).append('\n');
        sb.append("  name: ").append(q(lw.getName())).append('\n');

        Coordinates c = lw.getCoordinates();
        if (c != null) {
            sb.append("  coordinates: {\n");
            sb.append("    x: ").append(toStr(c.getX())).append(",\n");
            sb.append("    y: ").append(c.getY()).append('\n');
            sb.append("  }\n");
        } else {
            sb.append("  coordinates: null\n");
        }

        sb.append("  creationDate: ")
                .append(lw.getCreationDate() != null ? SDF.format(lw.getCreationDate()) : "null")
                .append('\n');

        sb.append("  minimalPoint: ").append(toStr(lw.getMinimalPoint())).append('\n');
        sb.append("  difficulty: ").append(toStr(lw.getDifficulty())).append('\n');

        Person a = lw.getAuthor();
        if (a != null) {
            sb.append("  author: {\n");
            sb.append("    name: ").append(q(a.getName())).append(",\n");
            sb.append("    height: ").append(toStr(a.getHeight())).append(",\n");
            sb.append("    eyeColor: ").append(toStr(a.getEyeColor())).append(",\n");
            sb.append("    hairColor: ").append(toStr(a.getHairColor())).append(",\n");
            sb.append("    nationality: ").append(toStr(a.getNationality())).append(",\n");

            Location loc = a.getLocation();
            if (loc != null) {
                sb.append("    location: {\n");
                sb.append("      x: ").append(loc.getX()).append(",\n");
                sb.append("      y: ").append(loc.getY()).append(",\n");
                sb.append("      z: ").append(loc.getZ()).append('\n');
                sb.append("    }\n");
            } else {
                sb.append("    location: null\n");
            }
            sb.append("  }\n");
        } else {
            sb.append("  author: null\n");
        }

        sb.append('}');
        return sb.toString();
    }

    /**
     * Собрать блок из списка LabWork с «глубоким» форматированием.
     */
    public static String joinLabWorksDeep(List<LabWork> items, boolean includeHeader) {
        if (items == null || items.isEmpty()) {
            return includeHeader ? "Всего элементов: 0\n" : "Ничего не найдено.";
        }
        String body = items.stream()
                .map(ResponseUtils::formatLabWorkDeep)
                .collect(Collectors.joining(SEP));
        if (!includeHeader) return body;
        return "Всего элементов: " + items.size() + "\n\n" + body;
    }

    /**
     * Сервис: склеить строки с заголовком.
     */
    public static String joinWithHeader(String header, List<String> lines) {
        String text = (lines == null ? List.<String>of() : lines).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        if (text.isBlank()) text = "Нет данных.";
        return (header == null || header.isBlank()) ? text : (header + "\n" + text);
    }

    private static String q(String s) {
        return s == null ? "null" : ('"' + s + '"');
    }

    private static String toStr(Object o) {
        return o == null ? "null" : String.valueOf(o);
    }
}
