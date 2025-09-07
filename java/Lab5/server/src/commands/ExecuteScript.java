package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CommandHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/**
 * Команда execute_script: считывает и выполняет команды из указанного файла-скрипта.
 * Реализует защиту от рекурсивных вызовов, ограничения глубины вложенности,
 * изоляцию ошибок по строкам и проверку параметров файла.
 */
public class ExecuteScript implements Command {
    private final CommandHandler handler;
    // Стек текущих запущенных скриптов для детекта циклов и глубины вложенности
    private final Deque<Path> callStack = new ArrayDeque<>();
    private static final int MAX_DEPTH = 10;
    // Максимальный размер скрипта в байтах (например, 1 МБ)
    private static final long MAX_FILE_SIZE = 1024L * 1024L;

    /**
     * @param handler обработчик команд, через который будут выполняться команды из скрипта
     */
    public ExecuteScript(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "Считать и исполнить скрипт из указанного файла";
    }

    @Override
    public Response execute(Request request) {
        String arg = request.getStringArgument();
        if (arg == null || arg.isBlank()) {
            return new Response(false, "Не указан путь до файла скрипта.");
        }
        Path script = Paths.get(arg).toAbsolutePath().normalize();
        // Проверка глубины вложенности
        if (callStack.size() >= MAX_DEPTH) {
            return new Response(false, "Превышена максимальная глубина вложенности скриптов (" + MAX_DEPTH + ")");
        }
        // Защита от рекурсии
        if (callStack.contains(script)) {
            return new Response(false, "Рекурсивный вызов скрипта запрещён: " + script);
        }
        // Проверка существования и доступности файла
        if (!Files.exists(script) || !Files.isRegularFile(script)) {
            return new Response(false, "Файл скрипта не найден: " + script);
        }
        if (!Files.isReadable(script)) {
            return new Response(false, "Нет прав на чтение файла: " + script);
        }
        try {
            long size = Files.size(script);
            if (size > MAX_FILE_SIZE) {
                return new Response(false, "Скрипт слишком большой (" + size + " байт), превышен лимит " + MAX_FILE_SIZE);
            }
        } catch (IOException e) {
            return new Response(false, "Не удалось получить размер файла: " + e.getMessage());
        }

        callStack.push(script);
        StringBuilder output = new StringBuilder();
        try (Scanner scanner = new Scanner(script)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                // Пропуск пустых строк и комментариев
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\s+", 2);
                String cmdName = parts[0];
                String cmdArg  = parts.length > 1 ? parts[1].trim() : null;
                Request inner = new Request(cmdName, cmdArg);
                try {
                    Response resp = handler.handle(inner);
                    output.append(resp.getMessage()).append(System.lineSeparator());
                } catch (Exception ex) {
                    output.append("Ошибка выполнения команды '")
                            .append(cmdName)
                            .append("': ")
                            .append(ex.getMessage())
                            .append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            return new Response(false, "Ошибка при чтении файла скрипта: " + e.getMessage());
        } finally {
            callStack.pop();
        }

        return new Response(true, output.toString().trim());
    }

    @Override
    public boolean modifiesCollection() {
        // сами команды внутри вызывают save, скрипт не меняет файл напрямую
        return false;
    }
}
