package Lab5.server.src.commands;

import Lab5.common.src.Request;
import Lab5.common.src.Response;

/**
 * Базовый интерфейс для всех команд.
 */
public interface Command {
    String getName();
    String getDescription();
    Response execute(Request request);
    default boolean modifiesCollection() {
        return false;
    }

}