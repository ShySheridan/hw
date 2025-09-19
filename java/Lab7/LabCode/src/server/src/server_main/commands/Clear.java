package server_main.commands;

import server_main.db.LabWorkDao;
import server_main.CollectionManager;
import server_main.CommandHandler;
import common_main.Request;
import common_main.Response;

import java.util.List;

/**
 * Удаляет ТОЛЬКО элементы, принадлежащие текущему пользователю.
 * Сначала удаляем из БД (по одному), и ТОЛЬКО при успехе — убираем из памяти.
 */
public class Clear implements Command {
    private final CollectionManager manager;
    private final LabWorkDao dao;

    public Clear(CollectionManager manager, LabWorkDao dao) {
        this.manager = manager;
        this.dao = dao;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "удалить все элементы, принадлежащие текущему пользователю";
    }

    @Override
    public Response execute(Request request) {
        var user = CommandHandler.UserContext.get();
        if (user == null) {
            return Response.fail("Требуется авторизация.");
        }

        // собираем id всех объектов текущего пользователя из ПАМЯТИ
        List<Long> myIds = manager.snapshot().stream()
                .filter(lw -> user.login().equals(lw.getOwnerLogin()))
                .map(lw -> lw.getId())
                .toList();

        if (myIds.isEmpty()) {
            return Response.ok("У вас нет элементов для удаления.");
        }

        int deleted = 0;
        for (Long id : myIds) {
            // 1) сначала БД
            boolean ok = dao.deleteById(id, user.id());
            if (ok) {
                // 2) затем память (обновляем только при успехе в БД)
                if (manager.removeById(id)) {
                    deleted++;
                }
            }
        }

        return Response.ok("Удалено элементов: " + deleted);
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }
}
