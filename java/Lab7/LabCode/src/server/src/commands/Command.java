package src.server.src.commands;

import src.common.src.Request;
import src.common.src.Response;

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