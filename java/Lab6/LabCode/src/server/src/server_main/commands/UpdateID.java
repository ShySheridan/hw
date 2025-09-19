package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;

public class UpdateID implements Command {
    private final CollectionManager manager;

    public UpdateID(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "update"; }
    @Override public String getDescription() { return "обновить значение элемента коллекции, id которого равен заданному"; }

    @Override
    public Response execute(Request request) {
        LabWork lw = request.getLabWork();
        if (lw == null) return Response.fail("Нужно передать объект с id.");
        if (lw.getId() <= 0) return Response.fail("Некорректный id.");

        boolean ok = manager.replaceById(lw);
        return ok ? Response.ok("Элемент с id=" + lw.getId() + " обновлён.")
                : Response.fail("Элемент с id=" + lw.getId() + " не найден.");
    }

    @Override public boolean modifiesCollection() { return true; }
}
