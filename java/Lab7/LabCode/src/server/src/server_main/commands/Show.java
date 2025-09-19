package server_main.commands;

import server_main.CollectionManager;
import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.utils.ResponseUtils;

import java.util.List;


public class Show implements Command {
    private final CollectionManager manager;

    public Show(CollectionManager manager) { this.manager = manager; }

    @Override public String getName() { return "show"; }
    @Override public String getDescription() { return "вывести все элементы коллекции (полностью, с вложенными полями)"; }

    @Override
    public Response execute(Request request) {
        List<LabWork> sorted = manager.getSorted();
        String body = ResponseUtils.joinLabWorksDeep(sorted, true);
        return Response.ok(body);
    }

}
