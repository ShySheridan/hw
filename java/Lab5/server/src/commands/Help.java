package Lab5.server.src.commands;
import Lab5.common.src.Request;
import Lab5.common.src.Response;


public class Help implements Command { // вывести справку по доступным командам
//    private final HashMap<String, String> commandMap;
    private final String ALLCOMMANDS = "count_by_author author: вывести количество элементов, значение поля author которых равно заданному\n" +
            "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
            "remove_greater {element}: удалить из коллекции все элементы, превышающие заданный\n" +
            "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
            "clear: очистить коллекцию\n" +
            "save: сохранить коллекцию в файл\n" +
            "update id {element}: обновить значение элемента коллекции, id которого равен заданному\n" +
            "add {element} : добавить новый элемент в коллекцию\n" +
            "exit: завершить программу (без сохранения в файл\n" +
            "remove_by_id id: удалить элемент из коллекции по его id\n" +
            "filter_starts_with_name name: вывести элементы, значения поля name которых начинается с заданной подстроки\n" +
            "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n" +
            "execute_script file_name: считать и исполнить скрипт из указанного файла\n" +
            "print_field_descending_author: вывести значение поля author всех элементов в порядке убывания\n" +
            "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и тд";

//    public Help() {}
//    public Help() {
//        this.commandMap = new HashMap<>();
//        fillCommandMap();
//    }
//
//    private void fillCommandMap() {
//        commandMap.put("add {element} ",
//                "добавить новый элемент в коллекцию");
//        commandMap.put("show" ,
//                "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
//        commandMap.put("info",
//                "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и тд");
//        commandMap.put("update id {element}",
//                "обновить значение элемента коллекции, id которого равен заданному");
//        commandMap.put("remove_by_id id",
//                "удалить элемент из коллекции по его id");
//        commandMap.put("clear",
//                "очистить коллекцию");
//        commandMap.put("save",
//                "сохранить коллекцию в файл");
//        commandMap.put("execute_script file_name",
//                "считать и исполнить скрипт из указанного файла");
//        commandMap.put("exit",
//                "завершить программу (без сохранения в файл");
//        commandMap.put("add_if_min {element}",
//                "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
//        commandMap.put("remove_greater {element}",
//                "удалить из коллекции все элементы, превышающие заданный");
//        commandMap.put("remove_lower {element}",
//                "удалить из коллекции все элементы, меньшие, чем заданный");
//        commandMap.put("count_by_author author",
//                "вывести количество элементов, значение поля author которых равно заданному");
//        commandMap.put("filter_starts_with_name name",
//                "вывести элементы, значения поля name которых начинается с заданной подстроки");
//        commandMap.put("print_field_descending_author",
//                "вывести значение поля author всех элементов в порядке убывания");
//    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Вывести справку по доступным командам";
    }

    @Override
    public Response execute(Request request){
//        for(Map.Entry<String, String> item : commandMap.entrySet()) {
//            System.out.println(item.getKey() + ": " + item.getValue());}
        return new Response(true, ALLCOMMANDS);
    };

}
