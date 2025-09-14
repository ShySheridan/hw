package Lab5.server.src.commands;

import Lab5.common.src.*;
import Lab5.common.src.enums.Color;
import Lab5.common.src.enums.Country;
import Lab5.common.src.enums.Difficulty;
import Lab5.server.src.CollectionManager;

import java.util.Scanner;

/**
 * Добавляет новый элемент в коллекцию
 */
public class Add implements Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }

    @Override
    public Response execute(Request request){
        LabWork labWork = request.getLabWork();
        if (labWork == null) {
            return new Response(false, "Введите название лабораторной работы.");
        }
        // Если id ещё не задан (нулевой), и дата не задана — генерируем их здесь
        if (labWork.getId() == 0) {
            labWork.createNewId();
        }
        if (labWork.getCreationDate() == null) {
            labWork.setCreationDate();
        }
        collectionManager.add(labWork);
        return new Response(true, "Лабораторная работа успешно добавлена");
    }

    @Override
    public boolean modifiesCollection() {
        return true;
    }

}

