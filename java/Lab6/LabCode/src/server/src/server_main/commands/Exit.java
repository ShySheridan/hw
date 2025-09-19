package server_main.commands;

import common_main.Request;
import common_main.Response;

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
        return new Response(true, "Завершение работы сервера.");
    }
}