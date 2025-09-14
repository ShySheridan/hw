package src.server.src.commands;

import src.common.src.LabWork;
import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;
import src.server.src.utils.ResponseUtils;

import java.util.List;

public class FilterStartsWithName implements Command {
    private final CollectionManager manager;

    public FilterStartsWithName(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "filter_starts_with_name"; }
    @Override public String getDescription() {
        return "вывести элементы, значение поля name которых начинается с заданной подстроки";
    }

    @Override
    public Response execute(Request request) {
        String pref = request.getStringArgument();
        if (pref == null || pref.isBlank()) return Response.fail("Нужно: filter_starts_with_name <prefix>");
        String p = pref.trim().toLowerCase();

        List<LabWork> result = manager.getLabWorks().stream()
                .filter(lw -> lw.getName() != null && lw.getName().toLowerCase().startsWith(p))
                .sorted()
                .toList();

        String msg = result.isEmpty()
                ? "Ничего не найдено."
                : "Найдено: " + result.size() + " элемент(ов).\n\n" + ResponseUtils.joinLabWorksDeep(result, false);

        return Response.ok(msg).setItems(result);
    }

}
