package Lab5.server.src.commands;

import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда print_field_descending_author: выводит значения поля author всех элементов
 * в порядке убывания (лексикографически по имени автора).
 * Usage: print_field_descending_author
 */
public class PrintFieldDescendingAuthor implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingAuthor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public String getName(){
        return "print_field_descending_author";
    }

    @Override
    public String getDescription(){
        return "вывести значения поля author всех элементов в порядке убывания";
    }

    @Override
    public Response execute(Request request){
        List<Person> authors = collectionManager.getLabWorks().stream()
                .map(LabWork::getAuthor)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Person::getName, String.CASE_INSENSITIVE_ORDER).reversed())
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Авторы: ").append("\n");
        for(Person author: authors) {
            sb.append("Author:\n")
                    .append("  Name: ").append(author.getName()).append("\n")
                    .append("  Height: ").append(author.getHeight()).append("\n")
                    .append("  Eye Color: ").append(author.getEyeColor()).append("\n")
                    .append("  Hair Color: ").append(author.getHairColor()).append("\n")
                    .append("  Nationality: ").append(author.getNationality()).append("\n")
                    .append("  Location: x=").append(author.getLocation().getX())
                    .append(", y=").append(author.getLocation().getY())
                    .append(", z=").append(author.getLocation().getZ()).append("\n");
        }
        return new Response(true, sb.toString().trim());
    }

}
