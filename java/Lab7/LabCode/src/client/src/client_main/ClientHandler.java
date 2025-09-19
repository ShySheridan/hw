package client_main;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Читает команды пользователя, формирует запросы (включая интерактивный ввод main.LabWork)
 * и отправляет их через main.ClientConnection.
 */
public class ClientHandler {
    private final ClientConnection connection;
    private final Scanner scanner = new Scanner(System.in);
    private final InputManager inputManager = new InputManager(scanner);

    private String login;
    private String password;

    private static final String[] COMMANDS = {
            "register",
            "help","info","show",
            "filter_starts_with_name","count_by_author","print_field_descending_author",
            "add","add_if_min","remove_greater","remove_lower","update","remove_by_id","clear",
            "execute_script","exit"
    };

    public ClientHandler(ClientConnection connection) {
        this.connection = connection;
    }

    public void start() {
        System.out.print("Логин: ");
        login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        password = scanner.nextLine();

        System.out.println("Введите команду (help — список). Для регистрации используйте: register");
        while (true) {
            System.out.print(">>> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            if (!handle(line)) break;
        }
    }

    private boolean handle(String input) {
        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : null;

        if (!Arrays.asList(COMMANDS).contains(cmd)) {
            System.out.println("Неизвестная команда. help — список команд.");
            return true;
        }

        if ("execute_script".equals(cmd)) {
            if (args == null || args.isBlank()) {
                System.out.println("Нужно: execute_script <file>");
                return true;
            }
            ScriptRunner runner = new ScriptRunner(connection, login, password);
            String result = runner.run(args.trim());
            System.out.println(result);
            return true;
        }

        Request req;
        switch (cmd) {
            case "register" -> {
                req = new Request("register");
            }
            case "add", "add_if_min", "remove_greater", "remove_lower" -> {
                LabWork lw = inputManager.buildLabWork();
                req = new Request(cmd, lw);
            }
            case "update" -> {
                if (args == null) { System.out.println("Нужно: update <id>"); return true; }
                long id;
                try { id = Long.parseLong(args.split("\\s+")[0]); }
                catch (NumberFormatException e) { System.out.println("ID должен быть числом."); return true; }
                LabWork lw = inputManager.buildLabWork();
                lw.setId(id);
                req = new Request(cmd, lw);
            }
            case "remove_by_id" -> {
                if (args == null) { System.out.println("Нужно: remove_by_id <id>"); return true; }
                long id;
                try { id = Long.parseLong(args); }
                catch (NumberFormatException e) { System.out.println("ID должен быть числом."); return true; }
                req = new Request(cmd, id);
            }
            case "filter_starts_with_name", "count_by_author" -> {
                if (args == null || args.isBlank()) {
                    System.out.println("Нужно передать строковый аргумент.");
                    return true;
                }
                req = new Request(cmd, args);
            }
            default -> {
                req = new Request(cmd);
            }
        }

        req.setLogin(login);
        req.setPassword(password);

        Response resp = connection.requestResponse(req);
        System.out.println(resp.getMessage());

        if (resp.getItems() != null && !resp.getItems().isEmpty()) {
            resp.getItems().forEach(System.out::println);
        }

        return !"exit".equals(cmd);
    }
}
