package src.server.src;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import src.common.src.Request;
import src.common.src.Response;
import src.server.src.commands.Command;
import src.server.src.security.AuthService;
import src.server.src.security.AuthService.AuthUser;

import java.util.*;

public class CommandHandler {
    private static final Logger log = LogManager.getLogger(CommandHandler.class);

    private final Map<String, Command> map = new HashMap<>();
    private final AuthService auth;

    private static final Set<String> PUBLIC = Set.of("help", "info", "register");

    public CommandHandler(CollectionManager cm, AuthService auth, Command... commands) {
        this.auth = auth;
        for (var c : commands) map.put(c.getName(), c);
    }
    public void registerCommand(Command c) { map.put(c.getName(), c); }

    public Response handle(Request r) {
        String name = r.getCommandName();
        var cmd = map.get(name);
        if (cmd == null) return Response.fail("Команда " + name + " не найдена");

        AuthService.AuthUser user = null;
        if (!PUBLIC.contains(name)) {
            var au = auth.authenticate(r);
            if (au.isEmpty()) return Response.fail("Требуется авторизация");
            user = au.get();
        }

        UserContext.set(user);
        try {
            var resp = cmd.execute(r);
            log.info("Executed '{}' by {}", name, user == null ? "anonymous" : user.login());
            return resp;
        } finally {
            UserContext.clear();
        }
    }

    public static final class UserContext {

        private static final ThreadLocal<AuthUser> TL = new ThreadLocal<>();
        public static void set(AuthService.AuthUser u) { TL.set(u); }
        public static AuthUser get() { return TL.get(); }
        public static void clear() { TL.remove(); }
    }
}
