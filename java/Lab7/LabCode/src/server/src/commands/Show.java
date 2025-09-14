package src.server.src.commands;

import src.common.src.LabWork;
import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;
import src.server.src.utils.ResponseUtils;

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
