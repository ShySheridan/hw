package Lab5.server.src;

/*Чтение запроса из requests.xml (команда от клиента).
Выполнение команды через CommandHandler.
Запись результата в responses.xml (ответ сервер отправляет клиенту).*/

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class CommandProcessor {
    /** Путь к файлу с запросами от клиента. */
    private static final Path REQUEST_PATH  =
            Paths.get("./Lab5/server/data/requests.xml");
    /** Путь к файлу для записи ответов клиенту. */
    private static final Path RESPONSE_PATH =
            Paths.get("./Lab5/server/data/responses.xml");

    private final CommandHandler commandHandler;
    public CommandProcessor(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Запускает цикл приёма и обработки команд.
     * <p>
     * Метод блокируется, ожидая появления новых XML-запросов,
     * затем обрабатывает их по очереди.
     */
    public void start() {
        System.out.println("Сервер готов к приёму команд...");
        while (true) {
            try {
                if (!Files.exists(REQUEST_PATH) || Files.size(REQUEST_PATH) == 0) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    continue;
                }

                Request request;
                try {
                    request = readRequest();
                    System.out.println(
                            "Запрос '" + request.getCommandName() +
                                    "' получен из файла: " + REQUEST_PATH
                    );

                } catch (RequestReadException e) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    continue;
                }

                Response response = commandHandler.handle(request);
                writeResponse(response);

                Files.write(REQUEST_PATH, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);

                if ("exit".equalsIgnoreCase(request.getCommandName())) {
                    System.out.println("Получена команда exit — завершаем работу сервера.");
                    break;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток прерван — завершаем работу.");
                break;
            } catch (IOException e) {
                System.err.println("Ошибка работы с файлом: " + e.getMessage());
            }
        }
    }

    /**
     * Читает и демаршалит XML-запрос из файла {@link #REQUEST_PATH}.
     *
     * @return объект Request, десериализованный из XML
     * @throws RequestReadException если не удалось корректно разобрать XML
     */
    private Request readRequest() throws RequestReadException {
        try {
            JAXBContext context = JAXBContext.newInstance(Request.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Request) unmarshaller.unmarshal((REQUEST_PATH).toFile());
        } catch (Exception e) {
            throw new RequestReadException("Ошибка при чтении файла requests.xml: " + e.getMessage());
        }
    }

    /**
     * Исключение, которое даёт более чистую и понятную обработку ситуации,
     * когда запрос ещё не успел дописаться в XML.
     */
    private static class RequestReadException extends Exception {
        public RequestReadException(String message) {
            super(message);
        }
    }

    /**
     * Сериализует объект {@link Response} и записывает его в файл {@link #RESPONSE_PATH}
     * с использованием буферизованного потока.
     *
     * @param response ответ сервера, подлежащий отправке клиенту
     */
    private void writeResponse(Response response) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
//      возвращает OutputStream, который создаёт responses.xml,
//      оборачивая его в BufferedOutputStream. мы добавляем внутренний буфер
//      это ускоряет запись и уменьшает число обращений к диску
                Files.newOutputStream(RESPONSE_PATH,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
//      Создаём контекст для Response и настраиваем маршаллер на отформатированный XML
            JAXBContext ctxRes = JAXBContext.newInstance(Response.class);
            Marshaller m = ctxRes.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//      JAXB преобразует объект response в XML и выводит его прямо в наш буферизованный поток bos
            m.marshal(response, bos);
        } catch (JAXBException | IOException e) {
            System.err.println("Не удалось сохранить ответ: " + e.getMessage());
        }
    }

}
