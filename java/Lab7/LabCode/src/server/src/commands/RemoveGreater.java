package src.server.src.commands;

import src.common.src.LabWork;
import src.common.src.Request;
import src.common.src.Response;
import src.server.src.CollectionManager;
import src.server.src.CommandHandler.UserContext;
import src.server.src.db.LabWorkDao;

import java.util.List;

/**
 * Удаляет все элементы, БОЛЬШЕ заданного, только для текущего пользователя.
 * Сначала удаляем из БД (по одному), и только при успехе — из памяти.
 */
public class RemoveGreater implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public RemoveGreater(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager;
        this.dao = dao;
    }

    @Override
    public String getName() { return "remove_greater"; }

    @Override
    public String getDescription() { return "удалить из коллекции элементы, превышающие заданный (только свои)"; }

    @Override
    public Response execute(Request request) {
        var user = UserContext.get();
        if (user == null) return Response.fail("Требуется авторизация.");

        LabWork ref = request.getLabWork();
        if (ref == null) return Response.fail("Нужно передать объект для сравнения.");

        // Ищем ТОЛЬКО свои элементы, которые > ref
        List<Long> idsToRemove = manager.snapshot().stream()
                .filter(lw -> user.login().equals(lw.getOwnerLogin()))
                .filter(lw -> lw.compareTo(ref) > 0)
                .map(LabWork::getId)
                .toList();

        if (idsToRemove.isEmpty()) {
            return Response.ok("Подходящих элементов не найдено.");
        }

        int removed = 0;
        for (Long id : idsToRemove) {
            // 1) сначала БД
            boolean ok = dao.deleteById(id, user.id());
            if (ok) {
                // 2) затем память
                if (manager.removeById(id)) removed++;
            }
        }
        return Response.ok("Удалено элементов: " + removed);
    }

    @Override
    public boolean modifiesCollection() { return true; }
}
