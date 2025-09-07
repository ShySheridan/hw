package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

public class RemoveByID implements Command {
    private final CollectionManager manager;

    public RemoveByID(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "удалить элемент по ID";
    }

    @Override
    public Response execute(Request request) {
        try {
            long id = Long.parseLong(request.getStringArgument());
            boolean removed = manager.removeById(id);
            return new Response(true, removed ? "Элемент удалён" : "Элемент не найден");
        } catch (NumberFormatException e) {
            return new Response(false, "ID должен быть числом");
        }
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }

}