package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;

public class Exit implements Command {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "завершить программу (без сохранения)";
    }

    @Override
    public Response execute(Request request) {
//        System.exit(0);
        return new Response(true, "Завершение работы сервера.");
    }
}