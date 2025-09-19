package server_main.commands;

import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;

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