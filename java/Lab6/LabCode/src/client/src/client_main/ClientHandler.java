package client_main;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;

import java.util.Arrays;
import java.util.Scanner;

public class ClientHandler {
    private final ClientConnection connection;
    private final Scanner scanner;
    private final InputManager inputManager;

    private static final String[] COMMANDS = {
            "add", "add_if_min", "clear", "count_by_author", "execute_script", "exit",
            "filter_starts_with_name", "help", "info", "print_field_descending_author",
            "remove_by_id", "remove_greater", "remove_lower", "show", "update"
    };

    public ClientHandler(ClientConnection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.inputManager = new InputManager(scanner);
    }

    public void start() {
        System.out.println("Введите команду. Введите 'help' для списка команд.");
        while (true) {
            System.out.print(">>> ");
            if (!scanner.hasNextLine()) {
                System.out.println("EOF");
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (!handleUserInput(input)) break;
        }
    }

    private boolean handleUserInput(String input) {
        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0];
        String args = parts.length > 1 ? parts[1].trim() : null;

        if (!Arrays.asList(COMMANDS).contains(cmd.toLowerCase())) {
            System.out.println("Неизвестная команда. Введите 'help'.");
            return true;
        }

        Request request;
        switch (cmd) {
            case "add", "add_if_min", "remove_greater", "remove_lower" -> {
                LabWork lw = inputManager.buildLabWork();
                request = new Request(cmd, lw);
            }
            case "update" -> {
                if (args == null) {
                    System.out.println("Нужно: update <id>");
                    return true;
                }
                long id;
                try {
                    id = Long.parseLong(args.split("\\s+")[0]);
                } catch (NumberFormatException e) {
                    System.out.println("ID должен быть числом.");
                    return true;
                }
                LabWork lw = inputManager.buildLabWork();
                lw.setId(id);
                request = new Request(cmd, lw);
            }
            case "remove_by_id" -> {
                if (args == null) {
                    System.out.println("Нужно: remove_by_id <id>");
                    return true;
                }
                long id;
                try {
                    id = Long.parseLong(args);
                } catch (NumberFormatException e) {
                    System.out.println("ID должен быть числом.");
                    return true;
                }
                request = new Request(cmd, id);
            }
            case "filter_starts_with_name", "count_by_author" -> {
                if (args == null || args.isBlank()) {
                    System.out.println("Нужно передать строковый аргумент.");
                    return true;
                }
                request = new Request(cmd, args);

            }
            case "execute_script" -> {
                if (args == null || args.isBlank()) {
                    System.out.println("Нужно: execute_script <file>");
                    return true;
                }
                request = new Request("execute_script", args.trim()); // ПЕРЕДАЁМ script2.txt
            }

            case "save" -> request = new Request("save"); // без аргументов


            default -> request = new Request(cmd);
        }

        Response resp = connection.requestResponse(request);
        System.out.println(resp.getMessage());

        // Если сервер прислал отсортированную коллекцию — можно вывести компакто
        if (resp.getItems() != null && !resp.getItems().isEmpty()) {
            resp.getItems().forEach(lw -> System.out.println("- " + lw.getName() + " (id=" + lw.getId() + ")"));
        }

        return !"exit".equalsIgnoreCase(cmd);
    }
}
