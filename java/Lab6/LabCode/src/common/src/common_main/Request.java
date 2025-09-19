package common_main;

import java.io.Serializable;

/**
 * Объект запроса от клиента к серверу.
 * Не используем JAXB — передаём по TCP как сериализованный объект.
 */
public class Request implements Serializable {


    private String commandName;
    private String stringArgument;
    private Long   longArgument;
    private LabWork labWork;

    public Request() {}

    public Request(String commandName) {
        this.commandName = commandName;
    }

    public Request(String commandName, LabWork labWork) {
        this.commandName = commandName;
        this.labWork = labWork;
    }

    public Request(String commandName, String stringArgument) {
        this.commandName = commandName;
        this.stringArgument = stringArgument;
    }

    public Request(String commandName, long longArgument) {
        this.commandName = commandName;
        this.longArgument = longArgument;
    }

    public String getCommandName() { return commandName; }
    public String getStringArgument() { return stringArgument; }

    public LabWork getLabWork() { return labWork; }
}
