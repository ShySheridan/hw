package src.server.src.commands;

import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CommandHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Выполняет скрипт на СЕРВЕРЕ.
 * Файл должен существовать на серверной машине.
 * Для каждой строки формируется вложенный Request с теми же login/password, что у внешнего.
 * Защита от рекурсии по именам файлов есть.
 */
public class ExecuteScript implements Command {
    private final CommandHandler handler;
    private static final ThreadLocal<Set<Path>> stack = ThreadLocal.withInitial(HashSet::new);

    public ExecuteScript(CommandHandler handler) {
        this.handler = handler;
    }

    @Override public String getName() { return "execute_script"; }
    @Override public String getDescription() { return "считать и исполнить скрипт из указанного файла на сервере"; }

    @Override
    public Response execute(Request request) {
        String file = request.getStringArgument();
        if (file == null || file.isBlank()) {
            return Response.fail("Нужно: execute_script <file>");
        }
        Path path = Path.of(file).normalize();

        Set<Path> st = stack.get();
        if (st.contains(path)) {
            return Response.fail("Обнаружена рекурсивная ссылка на скрипт: " + path);
        }

        try {
            if (!Files.exists(path)) {
                return Response.fail("Файл не найден на сервере: " + path);
            }
            st.add(path);

            List<String> lines = Files.readAllLines(path);
            StringBuilder out = new StringBuilder("Выполнение скрипта: ").append(path).append('\n');

            for (String raw : lines) {
                String line = raw == null ? "" : raw.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1].trim() : null;

                Request nested = fromLine(cmd, arg, request.getLogin(), request.getPassword());
                out.append("> ").append(line).append('\n');

                if (nested == null) {
                    out.append("  Неподдерживаемая команда в скрипте (или неверные аргументы).\n");
                    continue;
                }

                // Выполняем вложенную команду обычным путём (с авторизацией и правами)
                Response resp = handler.handle(nested);
                out.append("  ").append(resp.getMessage()).append('\n');
            }

            return Response.ok(out.toString());
        } catch (Exception e) {
            return Response.fail("Ошибка при выполнении скрипта: " + e.getMessage());
        } finally {
            st.remove(path);
            if (st.isEmpty()) stack.remove();
        }
    }

    @Override public boolean modifiesCollection() { return false; }

    /**
     * Сборка Request из одной строки скрипта.
     * Здесь поддержаны только команды БЕЗ LabWork-объекта.
     * add/update/remove_greater/remove_lower/add_if_min — намеренно не поддержаны,
     * чтобы не ломать формат. При необходимости можно расширить JSON-блоками.
     */
    private static Request fromLine(String cmd, String arg, String login, String password) {
        Request r;
        switch (cmd) {
            case "help", "info", "show", "clear", "exit",
                 "print_field_descending_author", "register" -> {
                r = new Request(cmd);
            }
            case "remove_by_id" -> {
                if (arg == null) return null;
                try {
                    long id = Long.parseLong(arg);
                    r = new Request(cmd, id);
                } catch (NumberFormatException e) { return null; }
            }
            case "filter_starts_with_name", "count_by_author", "execute_script" -> {
                if (arg == null || arg.isBlank()) return null;
                r = new Request(cmd, arg);
            }
            // Команды, требующие LabWork — не поддерживаем в этом варианте:
            case "add", "add_if_min", "remove_greater", "remove_lower", "update" -> {
                return null;
            }
            default -> { return null; }
        }
        r.setLogin(login);
        r.setPassword(password);
        return r;
    }
}
