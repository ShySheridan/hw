package src.common.src;

import java.io.Serializable;

/**
 * Объект запроса от клиента к серверу.
 */
public class Request implements Serializable {
    private String commandName;

    // Аргументы команд
    private String stringArgument;
    private Long longArgument;
    private LabWork labWork;

    // Аутентификация: логин и пароль передаём в КАЖДОМ запросе
    private String login;
    private String password; // хранится только для передачи (на сервере будет хэш)

    public Request() {}

    public Request(String commandName) { this.commandName = commandName; }
    public Request(String commandName, String stringArgument) { this.commandName = commandName; this.stringArgument = stringArgument; }
    public Request(String commandName, long longArgument) { this.commandName = commandName; this.longArgument = longArgument; }
    public Request(String commandName, LabWork labWork) { this.commandName = commandName; this.labWork = labWork; }

    public String getCommandName() { return commandName; }
    public String getStringArgument() { return stringArgument; }
    public Long getLongArgument() { return longArgument; }
    public LabWork getLabWork() { return labWork; }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
}