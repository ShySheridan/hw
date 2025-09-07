package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import Lab5.server.src.StorageManager;
import Lab5.server.src.CollectionManager;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Команда save: сохраняет коллекцию в storage.xml,
 * а затем делает буферизированную копию в output.xml.
 */
public class Save implements Command {
    private final StorageManager storageManager;
    private final CollectionManager collectionManager;

    private final Path outputPath;

    public Save(StorageManager sm, CollectionManager cm, Path outputPath) {
        this.storageManager    = sm;
        this.collectionManager = cm;
        this.outputPath        = outputPath;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Сохранить коллекцию в файл.";
    }

    @Override
    public Response execute(Request request) {
        // 1) Сохраняем в storage.xml
        storageManager.saveCollection(collectionManager.getLabWorks());

        // 2) Буферизированно копируем storage.xml → output.xml
        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(outputPath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            // читаем сразу из storageFile
            Files.copy(storageManager.getStoragePath(), bos);
        } catch (IOException e) {
            return new Response(false,
                    "Коллекция сохранена, но не удалось создать output.xml: " + e.getMessage());
        }

        return new Response(true,
                "Коллекция сохранена в storage.xml и скопирована в " + outputPath);
    }

    @Override
    public boolean modifiesCollection() {
        // сама коллекция в памяти не меняется этим методом
        return false;
    }
}
