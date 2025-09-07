package Lab5.client.src;

import Lab5.common.src.LabWork;
import Lab5.common.src.Request;
import Lab5.common.src.Response;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Читает команды пользователя, формирует запросы (включая интерактивный ввод LabWork)
 * и отправляет их через ClientConnection.
 */
public class ClientHandler {
    private final ClientConnection connection;
    private final Scanner scanner;
    private final InputManager inputManager;

    private static final String[] COMMANDS = {
            "add", "add_if_min", "clear", "count_by_author", "execute_script", "exit",
            "filter_starts_with_name", "help", "info", "print_field_descending_author",
            "remove_by_id", "remove_greater", "remove_lower", "save", "show", "update"
    };

    public ClientHandler(ClientConnection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.inputManager = new InputManager(scanner);
    }

    /**
     * Запускает главный цикл: читает строки команд, обрабатывает ввод, отправляет запросы.
     *
     */
    public void start() {
        System.out.println("Введите команду. Введите 'help' для списка команд.");
        try {
            while (true) {
                System.out.print(">>> ");
                if (!scanner.hasNextLine()) {
                    System.out.println("Завершение по Ctrl+D");
                    break;
                }
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                boolean cont = handleUserInput(input);
                if (!cont) break;
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * Обрабатывает одну введённую строку команды.
     *
     * @param input сырая строка с командой и аргументами
     * @return false, если нужно завершить (exit)
     */
    private boolean handleUserInput(String input) {
        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0];
        String args = parts.length > 1 ? parts[1].trim() : null;

        if (!isValidCommand(cmd)) {
            System.out.println("Неизвестная команда. Введите 'help'.");
            return true;
        }

        Request request;
        switch (cmd) {
            case "add": {
                LabWork lw = inputManager.buildLabWork();
                request = new Request(cmd, lw);
                break;
            }
            case "add_if_min": {
                LabWork lw = inputManager.buildLabWork();
                request = new Request(cmd, lw);
                break;
            }
            case "remove_greater":
                request = new Request(cmd, args);
                break;
            case "remove_lower": {
                // Интерактивно строим полный LabWork
//                LabWork lw = inputManager.buildLabWork();
//                request = new Request(cmd, lw);
                request = new Request(cmd, args);
                break;
            }
            case "update": {
                // update <id>
                if (args == null) {
                    System.out.println("Нужно указать ID: update <id>");
                    return true;
                }
                String[] upd = args.split("\\s+", 2);
                long id;
                try {
                    id = Long.parseLong(upd[0]);
                } catch (NumberFormatException e) {
                    System.out.println("ID должен быть числом.");
                    return true;
                }
                // собираем новую LabWork, сохраняя старый ID
                LabWork lw = inputManager.buildLabWork();
                lw.setId(id);
                request = new Request(cmd, lw);
                break;
            }
            default: {
                // Однострочные команды: строковый аргумент (или null)
                request = new Request(cmd, args);
            }
        }

        connection.sendRequest(request);
        Response response = connection.readResponseFromXml();
        System.out.println(response.getMessage());

        return !"exit".equalsIgnoreCase(cmd);
    }

    private boolean isValidCommand(String name) {
        return Arrays.asList(COMMANDS).contains(name.toLowerCase());
    }
}
