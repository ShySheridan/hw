package server_main.commands;


import common_main.Request;
import common_main.Response;

public class Help implements Command { // вывести справку по доступным командам
    private final String ALLCOMMANDS = """
            count_by_author author: вывести количество элементов, значение поля author которых равно заданному
            add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
            remove_greater {element}: удалить из коллекции все элементы, превышающие заданный
            show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении
            clear: очистить коллекцию
            update id {element}: обновить значение элемента коллекции, id которого равен заданному
            add {element} : добавить новый элемент в коллекцию
            exit: завершить программу (без сохранения в файл
            remove_by_id id: удалить элемент из коллекции по его id
            filter_starts_with_name name: вывести элементы, значения поля name которых начинается с заданной подстроки
            remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный
            execute_script file_name: считать и исполнить скрипт из указанного файла
            print_field_descending_author: вывести значение поля author всех элементов в порядке убывания
            info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и тд""";


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Вывести справку по доступным командам";
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, ALLCOMMANDS);
    }

}
