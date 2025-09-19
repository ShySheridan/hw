package server_main.commands;

import common_main.LabWork;
import common_main.Request;
import common_main.Response;
import server_main.db.LabWorkDao;
import server_main.*;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

/**
 * add_if_min {element}:
 * Добавляет элемент только если он строго меньше минимального в коллекции (по natural order).
 * Проверка делается по КОЛЛЕКЦИИ В ПАМЯТИ.
 * При добавлении: сначала БД (получаем id из sequence), затем обновляем память.
 */
public class AddIfMin implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public AddIfMin(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager;
        this.dao = dao;
    }

    @Override public String getName() { return "add_if_min"; }

    @Override public String getDescription() {
        return "добавить элемент, если он меньше минимального (только для текущего пользователя)";
    }

    @Override
    public Response execute(Request request) {
        var user = CommandHandler.UserContext.get();
        if (user == null) return Response.fail("Требуется авторизация.");

        LabWork lw = request.getLabWork();
        if (lw == null) return Response.fail("Не передан объект.");

        // Минимальный элемент в памяти
        Optional<LabWork> min = manager.snapshot().stream()
                .min(Comparator.naturalOrder());

        // Если коллекция не пуста и новый элемент НЕ меньше минимума — не добавляем
        if (min.isPresent() && lw.compareTo(min.get()) > 0) {
            return Response.ok("Элемент не добавлен: не меньше минимального.");
        }

        // Заполняем серверные поля
        lw.setCreationDate(new Date());
        lw.setOwnerLogin(user.login());

        // СНАЧАЛА БД
        var idOpt = dao.insert(lw, user.id(), user.login());
        if (idOpt.isEmpty()) return Response.fail("Не удалось сохранить в БД.");

        // id выдан sequence в БД
        lw.setId(idOpt.get());

        // ПОТОМ ПАМЯТЬ
        manager.add(lw);

        return Response.ok("Добавлено (add_if_min), id=" + lw.getId());
    }

    @Override public boolean modifiesCollection() { return true; }
}
