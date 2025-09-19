package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.CommandHandler;
import server_main.utils.LabReader;

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
    private final Deque<Path> callStack = new ArrayDeque<>();
    private static final int MAX_DEPTH = 10;
    private static final long MAX_FILE_SIZE = 1024L * 1024L;

    /**
     * @param handler обработчик команд, через который будут выполняться команды из скрипта
     */
    public ExecuteScript(CommandHandler handler) {
        this.handler = handler;
    }

    private LabWork read_lab_work(Scanner scanner) {
        LabReader reader = new LabReader(scanner);

        return reader.readLab();
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
        if (callStack.size() >= MAX_DEPTH) {
            return new Response(false, "Превышена максимальная глубина вложенности скриптов (" + MAX_DEPTH + ")");
        }
        if (callStack.contains(script)) {
            return new Response(false, "Рекурсивный вызов скрипта запрещён: " + script);
        }
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
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\s+", 2);
                String cmdName = parts[0];
                Request inner;
                if (cmdName.equals("add") || cmdName.equals("add_if_min") || cmdName.equals("remove_greater") || cmdName.equals("remove_lower") || cmdName.equals("update")) {
                    LabWork lab = read_lab_work(scanner);
                    if (cmdName.equals("update")) {
                        lab.setId(Long.parseLong(parts[1]));
                    }
                    inner = new Request(cmdName, lab);
                }else {
                    String cmdArg = parts.length > 1 ? parts[1].trim() : null;
                     inner = new Request(cmdName, cmdArg);
                }
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
        return false;
    }
}
