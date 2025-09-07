package Lab5.client.src;

import Lab5.common.src.Request;
import Lab5.common.src.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;

public class ClientConnection {
    private static final Path REQUEST_PATH  =
            Paths.get("./Lab5/server/data/requests.xml");
    private static final Path RESPONSE_PATH =
            Paths.get("./Lab5/server/data/responses.xml");

    /** Сериализует и отправляет запрос в requests.xml */
    public void sendRequest(Request request) {
        request.toXmlFile(REQUEST_PATH.toString());
    }

    /**
     * Ожидает непустой файл responses.xml, демаршалит из него Response и очищает файл.
     */
    public Response readResponseFromXml() {
        try {
            // 1) Ждём, пока файл появится и станет непустым
            for (int i = 0; i < 50; i++) {              // до ~10 секунд
                if (Files.exists(RESPONSE_PATH) && Files.size(RESPONSE_PATH) > 0) {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }

            // 2) Демаршалим XML в объект
            JAXBContext ctx = JAXBContext.newInstance(Response.class);
            Unmarshaller um = ctx.createUnmarshaller();
            Response response = (Response) um.unmarshal(RESPONSE_PATH.toFile());

            // 3) Очищаем файл, чтобы при следующем запросе не читать старый ответ
            Files.write(RESPONSE_PATH, new byte[0]);

            return response;
        } catch (JAXBException e) {
            e.printStackTrace();
            return new Response(FALSE, "Ошибка разбора ответа от сервера.");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return new Response(FALSE, "Ожидание ответа было прервано.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new Response(FALSE, "Ошибка работы с файлом ответа.");
        }
    }
}
