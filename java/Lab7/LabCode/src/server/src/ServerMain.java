package src.server.src;

import src.server.src.commands.*;
import src.server.src.db.Db;
import src.server.src.db.LabWorkDao;
import src.server.src.db.UserDao;
import src.server.src.net.TcpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import src.server.src.security.AuthService;

import java.util.HashSet;

public class ServerMain {
    private static final Logger log = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("log4j.configurationFile", "classpath:log4j2.xml");

        Db db = Db.fromEnv();
        var userDao = new UserDao(db);
        var lwDao   = new LabWorkDao(db);
        var auth    = new AuthService(userDao);

        var loaded = new HashSet<>(lwDao.loadAll());
        var collection = new CollectionManager(loaded);
        log.info("Loaded {} items from DB", collection.size());

        var handler = new CommandHandler(
                collection, auth,
                new Help(), new Info(collection), new Show(collection),
                new FilterStartsWithName(collection), new PrintFieldDescendingAuthor(collection),
                new Count_by_author(collection),
                new Add(collection, lwDao), new UpdateID(collection, lwDao),
                new AddIfMin(collection, lwDao),
                new RemoveByID(collection, lwDao),
                new RemoveGreater(collection, lwDao), new RemoveLower(collection, lwDao),
                new Clear(collection, lwDao),
                new Register(userDao),
                new Exit()
        );
        handler.registerCommand(new ExecuteScript(handler));

        int port = 5555;
        new TcpServer(port, handler).start();
    }
}
