package server_main.commands;


import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.db.LabWorkDao;
import server_main.*;

import java.util.Date;

public class Add implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public Add(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager; this.dao = dao;
    }

    @Override public String getName() { return "add"; }
    @Override public String getDescription() { return "добавить элемент (владелец = текущий пользователь)"; }

    @Override
    public Response execute(Request request) {
        var user = CommandHandler.UserContext.get();
        LabWork lw = request.getLabWork();
        if (lw == null) return Response.fail("Не передан объект.");
        // сервер выставляет служебные поля
        lw.setCreationDate(new Date());
        lw.setOwnerLogin(user.login());

        var idOpt = dao.insert(lw, user.id(), user.login());
        if (idOpt.isEmpty()) return Response.fail("Не удалось сохранить в БД.");
        lw.setId(idOpt.get());

        // ВАЖНО: обновляем память ТОЛЬКО при успехе БД
        manager.add(lw);
        return Response.ok("Добавлено, id=" + lw.getId());
    }

    @Override public boolean modifiesCollection() { return true; }
}
