package src.server.src.commands;

import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Команда info: выводит информацию о коллекции:
 * - тип внутреннего хранилища
 * - дата и время инициализации менеджера
 * - текущее количество элементов
 */
public class Info implements Command {
    private final CollectionManager collectionManager;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции";
    }

    @Override
    public Response execute(Request request) {
        String type = collectionManager.getCollectionType();
        Date initDate = collectionManager.getInitDate();
        int size = collectionManager.size();

        StringBuilder sb = new StringBuilder();
        sb.append("Информация о коллекции:\n");
        sb.append("Тип: ").append(type).append("\n");
        sb.append("Дата инициализации: ").append(DATE_FORMAT.format(initDate)).append("\n");
        sb.append("Количество элементов: ").append(size);

        return new Response(true, sb.toString());
    }
}

