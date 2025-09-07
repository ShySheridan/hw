package Lab5.server.src.commands;

import Lab5.common.src.*;
import Lab5.server.src.CollectionManager;


/**
 * Обновить значение элемента коллекции, id которого равен заданному
 */
public class UpdateID implements Command {
    private final CollectionManager collectionManager;
    private final Add addCommand;

    public UpdateID(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.addCommand = new Add(collectionManager);
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить элемент по ID";
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }

    @Override
    public Response execute(Request request) {
        LabWork updated = request.getLabWork();

        boolean replaced = collectionManager.replaceById(updated);
        if (!replaced) {
            return new Response(false, "Элемент с ID=" + updated.getId() + " не найден.");
        }
        return new Response(true, "LabWork с ID=" + updated.getId() + " успешно обновлён.");
    }

}
