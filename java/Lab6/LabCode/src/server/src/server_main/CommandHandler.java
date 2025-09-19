package server_main;

import common_main.Request;
import common_main.Response;
import server_main.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CommandHandler {
    private static final Logger log = LogManager.getLogger(CommandHandler.class);

    private final Map<String, Command> commandMap = new HashMap<>();
    private final CollectionManager collectionManager;
    private final StorageManager storageManager;

    public CommandHandler(CollectionManager cm, StorageManager sm, Command... commands) {
        this.collectionManager = cm;
        this.storageManager = sm;
        for (var c : commands) commandMap.put(c.getName(), c);
    }

    public void registerCommand(Command command) { commandMap.put(command.getName(), command); }

    public Response handle(Request request) {
        String name = String.valueOf(request.getCommandName());
        Command command = commandMap.get(name);
        if (command == null) {
            log.warn("Unknown command: {}", name);
            return Response.fail("Команда " + name + " не найдена");
        }
        long t0 = System.nanoTime();
        Response resp = command.execute(request);
        long dtMs = (System.nanoTime() - t0) / 1_000_000;
        log.info("Executed command '{}' in {} ms (success={})", name, dtMs, resp.isSuccess());

        if (resp.isSuccess() && command.modifiesCollection()) {
            storageManager.saveCollection(collectionManager.getLabWorks());
            log.info("Collection autosaved after modifying command '{}'", name);
        }
        return resp;
    }

    public void saveNow() {
        storageManager.saveCollection(collectionManager.getLabWorks());
        log.info("Collection saved (saveNow)");
    }

}
