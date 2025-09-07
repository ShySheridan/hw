package Lab5.client.src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClientMain {
    private static final Path REQUESTS = Paths.get("./Lab5/server/data/requests.xml");
    private static final Path RESPONSES = Paths.get("./Lab5/server/data/responses.xml");

    public static void main(String[] args) {
        try {
            Files.write(REQUESTS, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(RESPONSES, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Не удалось инициализировать XML-файлы: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        // Устанавливаем соединение для обмена XML-файлами
        ClientConnection connection = new ClientConnection();
        // Создаём обработчик пользовательского ввода
        ClientHandler handler = new ClientHandler(connection);
        // Запускаем основной цикл клиента
        handler.start();
    }
}
