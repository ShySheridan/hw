package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

public class Clear implements Command {
    private final CollectionManager manager;

    public Clear(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }

    @Override
    public Response execute(Request request) {
        manager.clear();
        return new Response(true, "Коллекция очищена.");
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }

}