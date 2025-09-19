package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;

public class Add implements Command {
    private final CollectionManager manager;

    public Add(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "add"; }
    @Override public String getDescription() { return "добавить новый элемент в коллекцию"; }

    @Override
    public Response execute(Request request) {
        LabWork lw = request.getLabWork();
        if (lw == null) return Response.fail("Не передан объект.");
        manager.addWithAutoFields(lw);
        return Response.ok("Элемент добавлен с id=" + lw.getId());
    }

    @Override public boolean modifiesCollection() { return true; }
}
