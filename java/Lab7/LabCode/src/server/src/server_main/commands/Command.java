package server_main.commands;

import common_main.Request;
import common_main.Response;

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