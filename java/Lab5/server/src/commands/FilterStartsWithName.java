package Lab5.server.src.commands;

import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class FilterStartsWithName implements Command {
    private final CollectionManager collectionManager;

    public FilterStartsWithName(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_starts_with_name";
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение имени автора которых начинается с заданной подстроки.";
    }

    @Override
    public Response execute(Request request){
        String prefix = request.getStringArgument();
        if (prefix == null || prefix.isEmpty()) {
            return new Response(false, "Название лабораторной работы не задано.");
        }

        // Фильтруем и сортируем
        TreeSet<LabWork> filtered = new TreeSet<>(collectionManager.getLabWorks().stream()
                .filter(lw -> lw.getName() != null && lw.getName().startsWith(prefix))
                .collect(Collectors.toSet())
        );

        if (filtered.isEmpty()) {
            return new Response(true,
                    "Элементы с именами, начинающимися на '" + prefix + "', не найдены.");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        for (LabWork lw : filtered) {
            sb.append("ID: ").append(lw.getId()).append("\n")
                    .append("Name: ").append(lw.getName()).append("\n")
                    .append("Coordinates: x=").append(lw.getCoordinates().getX())
                    .append(", y=").append(lw.getCoordinates().getY()).append("\n")
                    .append("Creation Date: ")
                    .append(lw.getCreationDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(dtf))
                    .append("\n")
                    .append("Minimal Point: ").append(lw.getMinimalPoint()).append("\n")
                    .append("Difficulty: ").append(lw.getDifficulty()).append("\n");

            if (lw.getAuthor() != null) {
                Person a = lw.getAuthor();
                sb.append("Author: ").append(a.getName()).append(", ")
                        .append("Height=").append(a.getHeight()).append(", ")
                        .append("Nationality=").append(a.getNationality()).append("\n");
            }

            sb.append("--------------------------------------------------\n");
        }

        return new Response(true, sb.toString().trim());
    }
}
