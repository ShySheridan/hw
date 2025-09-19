package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;

import java.util.List;

/**
 * Добавляет новый элемент в коллекцию, если его имя
 * лексикографически меньше имени минимального элемента.
 */
public class AddIfMin implements Command {
    private final CollectionManager collectionManager;

    public AddIfMin(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescription() {
        return "Добавить новый элемент, если его имя меньше минимального";
    }

    @Override
    public Response execute(Request request) {
        LabWork lw = request.getLabWork();
        if (lw == null) {
            return new Response(false, "Название предмета не передано.");
        }

        if (lw.getId() == 0) {
            lw.createNewId();
        }
        if (lw.getCreationDate() == null) {
            lw.setCreationDate();
        }

        List<LabWork> sorted = collectionManager.getSorted();
        if (!sorted.isEmpty()) {
            String minName = sorted.get(0).getName();
            if (lw.getName().compareToIgnoreCase(minName) >= 0) {
                return new Response(false,
                        "Элемент не добавлен: '" + lw.getName() +
                                "' не меньше минимального '" + minName + "'");
            }
        }

        collectionManager.add(lw);
        return new Response(true,
                "Лабораторная работа успешно добавлена с id=" + lw.getId());
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }
}
