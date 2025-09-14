package src.common.src;

import java.io.Serializable;
import java.util.List;

/**
 * Ответ сервера. Помимо сообщения может содержать отсортированный список элементов.
 */
public class Response implements Serializable {

    private boolean success;
    private String message;
    private List<LabWork> items; // опционально: «передаваемая клиенту коллекция (отсортирована)»

    public Response() {}
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public static Response ok(String msg) { return new Response(true, msg); }
    public static Response fail(String msg) { return new Response(false, msg); }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<LabWork> getItems() { return items; }

    public Response setItems(List<LabWork> items) { this.items = items; return this; }
}
