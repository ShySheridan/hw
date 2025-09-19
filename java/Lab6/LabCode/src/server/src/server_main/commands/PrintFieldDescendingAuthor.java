package server_main.commands;

import common_main.Request;
import common_main.Response;
import server_main.CollectionManager;
import server_main.utils.ResponseUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Команда print_field_descending_author: выводит значения поля author всех элементов
 * в порядке убывания (лексикографически по имени автора).
 * Usage: print_field_descending_author
 */
public class PrintFieldDescendingAuthor implements Command {
    private final CollectionManager manager;

    public PrintFieldDescendingAuthor(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "print_field_descending_author"; }
    @Override public String getDescription() {
        return "вывести значения поля author всех элементов в порядке убывания (по имени автора)";
    }

    @Override
    public Response execute(Request request) {
        List<String> authors = manager.getLabWorks().stream()
                .map(lw -> lw.getAuthor() == null ? null : lw.getAuthor().getName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(Comparator.<String>naturalOrder().reversed())
                .toList();

        String msg = ResponseUtils.joinWithHeader("Авторы (по убыванию):", authors);
        return Response.ok(msg);
    }

}