package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.db.LabWorkDao;
import server_main.*;

public class UpdateID implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public UpdateID(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager; this.dao = dao;
    }

    @Override public String getName() { return "update"; }
    @Override public String getDescription() { return "обновить элемент по id (только владелец)"; }

    @Override
    public Response execute(Request request) {
        var user = CommandHandler.UserContext.get();
        LabWork lw = request.getLabWork();
        if (lw == null || lw.getId() <= 0) return Response.fail("Нужно передать объект с корректным id.");

        // Проверим владельца в памяти
        var old = manager.findById(lw.getId());
        if (old.isEmpty()) return Response.fail("Элемент не найден.");
        if (!user.login().equals(old.get().getOwnerLogin()))
            return Response.fail("Недостаточно прав: элемент принадлежит " + old.get().getOwnerLogin());

        // Сохраняем служебные поля
        lw.setOwnerLogin(old.get().getOwnerLogin());
        lw.setCreationDate(old.get().getCreationDate());

        // Сначала БД
        boolean ok = dao.update(lw, user.id());
        if (!ok) return Response.fail("Не удалось обновить в БД (возможно, нет прав).");

        // Затем память
        manager.replaceById(lw);
        return Response.ok("Элемент обновлён.");
    }

    @Override public boolean modifiesCollection() { return true; }
}