package Lab5.common.src;
import jakarta.xml.bind.annotation.*;

/**
 * Объект, который передаётся от клиента серверу (содержит команду и данные).
 */
@XmlRootElement(name="request")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ LabWork.class })
public class Request {
    @XmlElement(name = "commandName")
    private String commandName;

    @XmlElementWrapper(name = "arguments")
    @XmlElement(name = "argument")
    private String[] arguments;

    @XmlElement(name = "labWork")
    private LabWork labWork;

    @XmlElement(name = "stringArgument")
    private String stringArgument;

    @XmlElement(name = "longArgument")
    private long longArgument;

    public Request(String commandName, LabWork labWork) {
        this.commandName = commandName;
        this.labWork = labWork;
    }

    public Request(String commandName, String[] arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public Request(String commandName, long longArgument) {
        this.commandName = commandName;
        this.longArgument = longArgument;
    }

    public Request(String commandName, String argument){
        this.commandName = commandName;
        this.stringArgument = argument;
    }

    // Пустой конструктор для JAXB
    public Request() {}

    // Геттеры и сеттеры
    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public LabWork getLabWork() {
        return labWork;
    }

    public void setLabWork(LabWork labWork) {
        this.labWork = labWork;
    }

    public String getStringArgument() {
        return stringArgument;
    }

    public void setStringArgument(String argument) {
        this.stringArgument = argument;
    }

    public long getLongArgument() {
        return longArgument;
    }

    public void setLongArgument(long longArgument) {
        this.longArgument = longArgument;
    }

    /**
     * Метод для добавления запроса в XML файл.
     * Если файл не существует, он будет создан, если существует — запрос будет добавлен.
     */
    public void toXmlFile(String filePath) {
        try {
            /** Создание JAXB контекста для сериализации объекта */
            jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(Request.class);
            jakarta.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            /** Запись объекта в файл */
            marshaller.marshal(this, new java.io.File(filePath));
//            System.out.println("Запрос записан в файл: " + filePath);
        } catch (jakarta.xml.bind.JAXBException e) {
            e.printStackTrace();
        }
    }

}