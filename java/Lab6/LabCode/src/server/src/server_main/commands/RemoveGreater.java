package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;

import java.util.List;

public class RemoveGreater implements Command {
    private final CollectionManager manager;

    public RemoveGreater(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "remove_greater"; }
    @Override public String getDescription() { return "удалить из коллекции элементы, превышающие заданный"; }

    @Override
    public Response execute(Request request) {
        LabWork ref = request.getLabWork();
        if (ref == null) return Response.fail("Нужно передать объект для сравнения.");

        List<LabWork> toRemove = manager.getLabWorks().stream()
                .filter(lw -> lw.compareTo(ref) > 0)
                .toList();

        int removed = manager.removeAll(toRemove);
        return Response.ok("Удалено элементов: " + removed);
    }

    @Override public boolean modifiesCollection() { return true; }
}
