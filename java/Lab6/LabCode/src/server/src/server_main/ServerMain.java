package server_main;

import common_main.LabWork;
import server_main.commands.*;
import server_main.net.TcpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class ServerMain {
    private static final Logger log = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("log4j2.contextSelector",
                "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        Path dataDir     = Paths.get("./data");
        Path storagePath = dataDir.resolve("storage.xml");
        Path inputPath   = dataDir.resolve("input1000.xml");

        StorageManager storage = new StorageManager(storagePath, inputPath);
        HashSet<LabWork> storageData = storage.loadFromStorageFile();
        HashSet<LabWork> fromInput   = storage.loadFromInputFile();
        storageData.addAll(fromInput);

        var collection = new CollectionManager(storageData);
        log.info("Collection initialized: size={}, initDate={}", collection.size(), collection.getDate());

        var handler = new CommandHandler(
                collection, storage,
                new RemoveByID(collection),
                new Clear(collection),
                new AddIfMin(collection),
                new RemoveGreater(collection),
                new Add(collection),
                new UpdateID(collection),
                new RemoveLower(collection),
                new Count_by_author(collection),
                new FilterStartsWithName(collection),
                new PrintFieldDescendingAuthor(collection),
                new Help(),
                new Info(collection),
                new Exit(),
                new Show(collection)
        );
        handler.registerCommand(new ExecuteScript(handler));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            storage.saveCollection(collection.getLabWorks());
            log.info("Collection saved on shutdown");
        }));

        int port = 5555;
        new TcpServer(port, handler).start();
    }
}
