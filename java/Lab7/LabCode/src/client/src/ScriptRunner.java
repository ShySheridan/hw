package src.client.src;

import src.common.src.*;
import src.common.src.enums.Color;
import src.common.src.enums.Country;
import src.common.src.enums.Difficulty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Исполнитель скриптов (КЛИЕНТ).
 *   add / add_if_min / remove_greater / remove_lower
 *     затем 13 строк с полями LabWork в порядке, как в прошлых лаб:
 *       1) name (String)
 *       2) coordinates.x (Double)
 *       3) coordinates.y (float)
 *       4) minimalPoint (Float)
 *       5) difficulty (число 1..N по порядку перечисления)
 *       6) author.name (String)
 *       7) author.height (Integer)
 *       8) author.eyeColor (число 1..N)
 *       9) author.hairColor (число 1..N)
 *      10) author.nationality (число 1..N)
 *      11) author.location.x (double)
 *      12) author.location.y (float)
 *      13) author.location.z (double)
 *   update <id> затем те же 13 строк
 *   remove_by_id <id>
 *   filter_starts_with_name <prefix>
 *   count_by_author <name>
 *   show / info / clear / print_field_descending_author
 *   execute_script <file> (рекурсия поддерживается с защитой)
 */
public final class ScriptRunner {
    private final ClientConnection connection;
    private final String login;
    private final String password;

    private final Deque<Path> stack = new ArrayDeque<>();

    public ScriptRunner(ClientConnection connection, String login, String password) {
        this.connection = connection;
        this.login = login;
        this.password = password;
    }

    public String run(String file) {
        Path path = Path.of(file).normalize();
        if (stack.contains(path)) {
            return "Обнаружена рекурсивная ссылка на скрипт: " + path;
        }
        try {
            if (!Files.exists(path)) {
                return "Файл не найден: " + path;
            }
            stack.push(path);
            List<String> lines = Files.readAllLines(path).stream()
                    .map(l -> l == null ? "" : l.trim())
                    .collect(Collectors.toList());

            StringBuilder out = new StringBuilder("Выполнение скрипта: ").append(path).append('\n');
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0].toLowerCase(Locale.ROOT);
                String inlineArg = (parts.length > 1 ? parts[1].trim() : null);

                out.append("> ").append(line).append('\n');

                try {
                    switch (cmd) {
                        case "add", "add_if_min", "remove_greater", "remove_lower" -> {
                            if (i + 13 >= lines.size()) {
                                out.append("  Недостаточно данных для объекта (ожидалось 13 строк).\n");
                                continue;
                            }
                            List<String> slice = lines.subList(i + 1, i + 14);
                            LabWork lw = parseLabWork(slice);
                            i += 13;
                            Request r = new Request(cmd, lw);
                            attachAuth(r);
                            Response resp = connection.requestResponse(r);
                            out.append("  ").append(resp.getMessage()).append('\n');
                        }
                        case "update" -> {
                            Long id = null;
                            if (inlineArg != null && !inlineArg.isBlank()) {
                                id = tryParseLong(inlineArg);
                            } else if (i + 1 < lines.size()) {
                                id = tryParseLong(lines.get(++i));
                            }
                            if (id == null || id <= 0) {
                                out.append("  Нужно: update <id> и 13 строк с полями объекта.\n");
                                continue;
                            }
                            if (i + 13 >= lines.size()) {
                                out.append("  Недостаточно данных для объекта (ожидалось 13 строк).\n");
                                continue;
                            }
                            List<String> slice = lines.subList(i + 1, i + 14);
                            LabWork lw = parseLabWork(slice);
                            lw.setId(id);
                            i += 13;
                            Request r = new Request("update", lw);
                            attachAuth(r);
                            Response resp = connection.requestResponse(r);
                            out.append("  ").append(resp.getMessage()).append('\n');
                        }
                        case "remove_by_id" -> {
                            Long id = (inlineArg != null && !inlineArg.isBlank())
                                    ? tryParseLong(inlineArg)
                                    : (i + 1 < lines.size() ? tryParseLong(lines.get(++i)) : null);
                            if (id == null || id <= 0) {
                                out.append("  Нужно: remove_by_id <id>\n");
                                continue;
                            }
                            Request r = new Request("remove_by_id", id);
                            attachAuth(r);
                            Response resp = connection.requestResponse(r);
                            out.append("  ").append(resp.getMessage()).append('\n');
                        }
                        case "filter_starts_with_name", "count_by_author" -> {
                            String arg = (inlineArg != null && !inlineArg.isBlank())
                                    ? inlineArg
                                    : (i + 1 < lines.size() ? lines.get(++i) : null);
                            if (arg == null || arg.isBlank()) {
                                out.append("  Нужен строковый аргумент.\n");
                                continue;
                            }
                            Request r = new Request(cmd, arg);
                            attachAuth(r);
                            Response resp = connection.requestResponse(r);
                            out.append("  ").append(resp.getMessage()).append('\n');
                        }
                        case "help", "info", "show", "clear",
                             "print_field_descending_author", "exit" -> {
                            Request r = new Request(cmd);
                            attachAuth(r);
                            Response resp = connection.requestResponse(r);
                            out.append("  ").append(resp.getMessage()).append('\n');
                        }
                        case "execute_script" -> {
                            String nextFile = (inlineArg != null && !inlineArg.isBlank())
                                    ? inlineArg
                                    : (i + 1 < lines.size() ? lines.get(++i) : null);
                            if (nextFile == null || nextFile.isBlank()) {
                                out.append("  Нужно: execute_script <file>\n");
                                continue;
                            }
                            out.append("  ").append(run(nextFile)).append('\n');
                        }
                        default -> out.append("  Неизвестная/неподдерживаемая команда в скрипте.\n");
                    }
                } catch (Exception e) {
                    out.append("  Ошибка: ").append(e.getMessage()).append('\n');
                }
            }

            return out.toString();
        } catch (Exception e) {
            return "Ошибка чтения скрипта: " + e.getMessage();
        } finally {
            if (!stack.isEmpty() && stack.peek().equals(path)) stack.pop();
        }
    }


    private static Long tryParseLong(String s) {
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return null; }
    }
    private static Integer tryParseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }
    private static Float tryParseFloat(String s) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return null; }
    }
    private static Double tryParseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }

    private static <E extends Enum<E>> E enumByIndex(Class<E> clazz, String s) {
        int idx = Math.max(1, tryParseInt(s) == null ? 1 : tryParseInt(s));
        E[] vals = clazz.getEnumConstants();
        if (idx > vals.length) idx = vals.length;
        return vals[idx - 1];
    }

    private static LabWork parseLabWork(List<String> v) {
        if (v.size() < 13) throw new IllegalArgumentException("ожидалось 13 строк для объекта");
        int i = 0;
        String name = v.get(i++);

        Double cx = notNull(tryParseDouble(v.get(i++)), "coordinates.x");
        Float cy  = notNull(tryParseFloat (v.get(i++)), "coordinates.y");

        Float minimalPoint = notNull(tryParseFloat(v.get(i++)), "minimalPoint");
        Difficulty difficulty = enumByIndex(Difficulty.class, v.get(i++));

        String authorName = v.get(i++);
        Integer height    = notNull(tryParseInt(v.get(i++)), "author.height");
        Color eye         = enumByIndex(Color.class, v.get(i++));
        Color hair        = enumByIndex(Color.class, v.get(i++));
        Country nat       = enumByIndex(Country.class, v.get(i++));

        Double lx = notNull(tryParseDouble(v.get(i++)), "location.x");
        Float  ly = notNull(tryParseFloat (v.get(i++)), "location.y");
        Double lz = notNull(tryParseDouble(v.get(i++)), "location.z");

        Coordinates coords = new Coordinates(cx, cy);
        Location loc = new Location(lx, ly, lz);
        Person person = new Person(authorName, height, eye, hair, nat, loc);

        LabWork lw = new LabWork();
        lw.setName(name);
        lw.setCoordinates(coords);
        lw.setMinimalPoint(minimalPoint);
        lw.setDifficulty(difficulty);
        lw.setAuthor(person);
        return lw;
    }

    private static <T> T notNull(T v, String field) {
        if (v == null) throw new IllegalArgumentException("некорректное значение поля " + field);
        return v;
    }

    private void attachAuth(Request r) {
        r.setLogin(login);
        r.setPassword(password);
    }
}
