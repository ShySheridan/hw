package src.server.src.commands;

import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;
import src.server.src.CommandHandler;
import src.server.src.db.LabWorkDao;

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