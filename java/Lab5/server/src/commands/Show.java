package Lab5.server.src.commands;

import Lab5.common.src.LabWork;
import Lab5.common.src.Person;
import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.CollectionManager;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

public class Show implements Command {
    private final CollectionManager manager;
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Show(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "показать все элементы коллекции в порядке сортировки";
    }

    @Override
    public Response execute(Request request) {
        TreeSet<LabWork> sorted = new TreeSet<>(manager.getLabWorks());
        if (sorted.isEmpty()) return new Response(true, "Коллекция пуста");


//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        for (LabWork lw : sorted) {
            sb.append("ID: ").append(lw.getId()).append("\n")
                    .append("Name: ").append(lw.getName()).append("\n")
                    .append("Coordinates: x=").append(lw.getCoordinates().getX())
                    .append(", y=").append(lw.getCoordinates().getY()).append("\n")
                    .append("Creation Date: ").append(SDF.format(lw.getCreationDate())).append("\n")
                    .append("Minimal Point: ").append(lw.getMinimalPoint()).append("\n")
                    .append("Difficulty: ").append(lw.getDifficulty()).append("\n");

            Person author = lw.getAuthor();
            if (author != null) {
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

            sb.append("--------------------------------------------------\n");
        }

        return new Response(true, sb.toString().trim());
    }
}

//TODO в инпут файле прописать цвет глаз и тд иначе выводится нуллл