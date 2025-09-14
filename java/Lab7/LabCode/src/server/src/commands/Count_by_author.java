package src.server.src.commands;

import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;

public class Count_by_author implements Command {
    private final CollectionManager manager;

    public Count_by_author(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "count_by_author"; }
    @Override public String getDescription() { return "вывести количество элементов, у которых author равен заданному имени"; }

    @Override
    public Response execute(Request request) {
        String authorName = request.getStringArgument();
        if (authorName == null || authorName.isBlank()) {
            return Response.fail("Нужно: count_by_author <name>");
        }
        String needle = authorName.trim();

        long count = manager.getLabWorks().stream()
                .filter(lw -> lw.getAuthor() != null
                        && lw.getAuthor().getName() != null
                        && lw.getAuthor().getName().trim().equalsIgnoreCase(needle))
                .count();

        return Response.ok("Количество элементов с автором '" + needle + "': " + count);
    }

}
