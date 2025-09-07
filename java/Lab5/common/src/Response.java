package Lab5.common.src;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.*;

/**
 * Объект, который сервер отправляет клиенту с результатом выполнения команды.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {
    @XmlElement(name = "message")
    private String message;
    private boolean success;

    public Response() {}

    public Response(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

/**
 * Сериализует этот ответ в XML и сохраняет его в указанный файл.
 *
 * @param filePath путь к файлу; если файл существует, будет перезаписан, иначе — создан
 **/
    public void toXmlFile(String filePath) {
        try {
            // Создание JAXB контекста для сериализации объекта
            jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(Response.class);
            jakarta.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            setSuccess(Boolean.TRUE);

            // Запись объекта в файл
            marshaller.marshal(this, new java.io.File(filePath));
            System.out.println("Ответ записан в файл: " + filePath);
        } catch (jakarta.xml.bind.JAXBException e) {
            setSuccess(Boolean.FALSE);
            e.printStackTrace();
        }
    }
}
