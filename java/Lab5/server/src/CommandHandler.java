package Lab5.server.src;


import Lab5.server.src.commands.Command;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Класс-обработчик команд. Регистрирует и выполняет команды по их имени.
 */
public class CommandHandler {
    private final CollectionManager collectionManager;
    private final StorageManager   storageManager;
    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandHandler(CollectionManager collectionManager, StorageManager storageManager,
            Command... commands) {
        this.collectionManager = collectionManager;
        this.storageManager = storageManager;
        for (Command command : commands) {
            registerCommand(command);
        }
    }
    /**
     * Регистрация команды: сохраняем в map по ключу имени.
     * @param command объект команды
     */
    public void registerCommand(Command command) {
        String name = command.getName();
        if (commandMap.containsKey(name)) {
            throw new IllegalArgumentException("Команда " + name + " уже зарегистрирована");
        }
        commandMap.put(name, command);
    }

    /**
     * Обработка запроса: находим команду по имени и вызываем ее реализацию
     * @param request входной запрос с именем команды и параметрами
     * @return ответ выполнения или сообщение об ошибке
     */
    public Response handle(Request request) {
        String commandName = request.getCommandName();
        Command command = commandMap.get(commandName);
        if (command == null) {
            return new Response(false, "Команда " + commandName + " не найдена");
        }
        try {
            Response resp = command.execute(request);
            // если команда изменила коллекцию — сохраняем сразу
            if (resp.isSuccess() && command.modifiesCollection()) {
                storageManager.saveCollection(collectionManager.getLabWorks());
            }
            return resp;
        } catch (Exception e) {
            return new Response(false, "Ошибка выполнения команды: " + e.getMessage());
        }
    }

    /**
     * Возвращает имена всех зарегистрированных команд.
     */
    public Set<String> getRegisteredCommands() {
        return Collections.unmodifiableSet(commandMap.keySet());
    }

}
