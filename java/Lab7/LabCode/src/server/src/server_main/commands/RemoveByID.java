package server_main.commands;

import server_main.db.LabWorkDao;
import server_main.CollectionManager;
import server_main.CommandHandler;
import common_main.Request;
import common_main.Response;

public class RemoveByID implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public RemoveByID(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager; this.dao = dao;
    }

    @Override public String getName() { return "remove_by_id"; }
    @Override public String getDescription() { return "удалить элемент по id (только владелец)"; }

    @Override
    public Response execute(Request request) {
        var user = CommandHandler.UserContext.get();
        Long id = request.getLongArgument();
        if (id == null || id <= 0) return Response.fail("Нужно: remove_by_id <id>");

        var lw = manager.findById(id);
        if (lw.isEmpty()) return Response.fail("Элемент не найден.");
        if (!lw.get().getOwnerLogin().equals(user.login()))
            return Response.fail("Недостаточно прав: владелец " + lw.get().getOwnerLogin());

        boolean ok = dao.deleteById(id, user.id());
        if (!ok) return Response.fail("Не удалось удалить в БД.");

        manager.removeById(id);
        return Response.ok("Удалено.");
    }

    @Override public boolean modifiesCollection() { return true; }
}