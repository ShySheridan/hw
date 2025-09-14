package Lab5.server.src;

import Lab5.common.src.LabWork;
import Lab5.server.src.*;
import Lab5.server.src.commands.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

public class ServerMain {
//    private static Path STORAGE_PATH = Paths.get("/Users/amina/IdeaProjects/hw/Lab5/server/data/storage.xml");
    // Получаем путь к input.xml из переменной окружения
//    private static Path INPUT_PATH = Paths.get(System.getenv("LABWORK_DATA_FILE"));

    public static void main(String[] args) {
        System.out.println("Запуск сервера");

        String inputPathStr = System.getenv("LABWORK_DATA_FILE");
        if (inputPathStr == null || inputPathStr.isBlank()) {
            System.err.println("Переменная окружения LABWORK_DATA_FILE не установлена.");
            System.exit(1);
        }
        Path inputPath = Paths.get(inputPathStr);
        Path dataDir       = Paths.get("./Lab5/server/data");
        Path storagePath   = dataDir.resolve("storage.xml");
        Path requestsPath  = dataDir.resolve("requests.xml");
        Path responsesPath = dataDir.resolve("responses.xml");
        Path outputPath    = dataDir.resolve("output.xml");

        try {
            Files.createDirectories(dataDir);
            for (Path p : new Path[]{storagePath, requestsPath, responsesPath, outputPath}) {
                Files.write(p,
                        new byte[0],
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Не удалось очистить файлы при старте: " + e.getMessage());
        }

        StorageManager storageManager = new StorageManager(storagePath, inputPath);
        HashSet<LabWork> storageData = storageManager.loadFromStorageFile();

//        Добавляем данные из input.xml
        HashSet<LabWork> fromInput = storageManager.loadFromInputFile();
        storageData.addAll(fromInput);

        System.out.println("Loaded from storage.xml: " + storageData.size());
        System.out.println("Loaded from input.xml:   " + fromInput.size());

        CollectionManager collectionManager = new CollectionManager();
        collectionManager.setInitialData(storageData);

        System.out.println("Loaded from storage.xml: " + storageData.size());
        System.out.println("Loaded from input.xml:   " + fromInput.size());

        CommandHandler handler = new CommandHandler(
                collectionManager,
                storageManager,
                new RemoveByID(collectionManager),
                new Clear(collectionManager),
                new AddIfMin(collectionManager),
                new RemoveGreater(collectionManager),
                new Add(collectionManager),
                new UpdateID(collectionManager),
                new RemoveLower(collectionManager),
                new Count_by_author(collectionManager),
                new FilterStartsWithName(collectionManager),
                new PrintFieldDescendingAuthor(collectionManager),
                new Help(),
                new Info(collectionManager),
                new Exit(),
                new Show(collectionManager));
        handler.registerCommand(new ExecuteScript(handler));
        handler.registerCommand(new Save(storageManager, collectionManager, outputPath));



        // Запускаем цикл обработки
        CommandProcessor processor = new CommandProcessor(handler);
        processor.start();
        System.out.println("Сервер корректно остановлен.");



    }
}

