package Lab5.server.src.commands;

import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

import java.util.Objects;

/**
 * Команда count_by_author: выводит количество элементов,
 * значение поля author.name которых равно заданному.
 * Usage: count_by_author <authorName>
 */
public class Count_by_author implements Command {
    private CollectionManager collectionManager;

    public Count_by_author(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName(){
        return "count_by_author";
    }

    @Override
    public String getDescription() {
        return "Вывести количество элементов, значение поля author которых равно заданному.";
    }

    @Override
    public Response execute(Request request){
        String[] raw = request.getArguments();
        String authorName = (raw == null || raw.length == 0)
                ? request.getStringArgument()          // fallback, if you used that ctor
                : String.join(" ", raw).trim();

        if (authorName == null || authorName.isEmpty()) {
            return new Response(false, "Не указан author для поиска.");
        }

        try{
            long count = collectionManager.getLabWorks().stream()
                    .map(LabWork::getAuthor)             // Person или null
                    .filter(Objects::nonNull)
                    .map(Person::getName)       // String или maybe null
                    .filter(Objects::nonNull)
                    .filter(name -> name.equalsIgnoreCase(authorName))
                    .count();
            return new Response(true,
                    "Количество элементов с автором '" + authorName + "': " + count);
        } catch (Exception e) {
            return new Response(false, "Ошибка при добавлении: " + e.getMessage());
        }

    }
}

