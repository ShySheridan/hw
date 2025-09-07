package Lab5.server.src.commands;

import Lab5.common.src.LabWork;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class RemoveGreater implements Command {
    private final CollectionManager collectionManager;

    public RemoveGreater(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public String getName(){
        return "remove_greater";
    }

    @Override
    public String getDescription(){
        return "удалить из коллекции все элементы, превышающие заданный.";
    }

    @Override
    public Response execute(Request request) {
        LabWork reference = request.getLabWork();
        if (reference == null) {
            String name = request.getStringArgument();
            if (name == null || name.isBlank()) {
                return new Response(false,
                        "Нужно передать элемент или имя: remove_greater <name> ИЛИ интерактивно ввести {element}");
            }
            reference = new LabWork();     // достаточно имени, compareTo сравнивает по name
            reference.setName(name);
        }

        TreeSet<LabWork> sorted = new TreeSet<>(Comparator.comparing(LabWork::getName));
        sorted.addAll(collectionManager.getLabWorks());
        SortedSet<LabWork> greater = sorted.tailSet(reference, false);

        int removed = collectionManager.removeAll(greater);
        return new Response(true, "Удалено элементов: " + removed);
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }

}
