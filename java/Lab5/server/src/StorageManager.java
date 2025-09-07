package Lab5.server.src;

import Lab5.common.src.LabWork;
import Lab5.common.src.LabWorkList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Управляет постоянным хранением коллекции в storage.xml.
 * <p>
 * При создании экземпляра класс очищает или создаёт storage.xml,
 * затем записывает туда данные из input.xml (если они есть) с использованием буферизированного потока.
 * После каждой команды модификации коллекции следует вызывать {@link #saveCollection(Collection)}.
 * Для полного удаления содержимого файла (например, при выходе) использовать {@link #clearStorage()}.
 */
public class StorageManager {
    private final Path storagePath;
    private final Path inputPath;
    private final File storageFile;
    private final File inputFile;
    private final JAXBContext jaxbContext;

    public StorageManager(Path storageFilePath, Path inputFilePath) {
        this.storagePath = storageFilePath;
        this.inputPath   = inputFilePath;
        this.storageFile = storagePath.toFile();
        this.inputFile   = inputPath.toFile();
        try {
            // Инициализируем JAXB для обёртки LabWorkList
            this.jaxbContext = JAXBContext.newInstance(LabWorkList.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Не удалось инициализировать JAXBContext", e);
        }

        // Подготовка и очистка файла storage.xml через буферизированный поток
        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(storagePath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            // просто открываем и закрываем, чтобы создать/очистить файл
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подготовить storage.xml: " + e.getMessage(), e);
        }

        // Загружаем начальные данные из input.xml и, если есть, сразу сохраняем
        HashSet<LabWork> initial = loadFromInputFile();
//        if (!initial.isEmpty()) {
//            saveCollection(initial);
//        }
    }

    /**
     * Читает и демаршалит данные из input.xml в набор LabWork.
     */
    public HashSet<LabWork> loadFromInputFile() {
        if (!inputFile.exists()) {
            return new HashSet<>();
        }
        try (Scanner scanner = new Scanner((inputFile))) {
            StringBuilder xml = new StringBuilder();
            while (scanner.hasNextLine()) {
                xml.append(scanner.nextLine()).append("\n");
            }
            Unmarshaller um = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml.toString());
            LabWorkList list = (LabWorkList) um.unmarshal(reader);
            for (LabWork lw : list.getLabWorks()) {
                lw.createNewId();
                lw.setCreationDate();
            }
            return new HashSet<>(list.getLabWorks());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки начального файла input.xml: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Загружает текущую коллекцию из storage.xml.
     */
    public HashSet<LabWork> loadFromStorageFile() {
        if (!storageFile.exists() || storageFile.length() == 0) {
            return new HashSet<>();
        }
        try {
            Unmarshaller um = jaxbContext.createUnmarshaller();
            LabWorkList list = (LabWorkList) um.unmarshal(storageFile);
            return new HashSet<>(list.getLabWorks());
        } catch (JAXBException e) {
            System.err.println("Warning: неверный формат storage.xml, возвращаем пустую коллекцию.");
            return new HashSet<>();
        }
    }

    /**
     * Сохраняет текущую коллекцию в storage.xml через буферизированный поток.
     * @param collection коллекция для сохранения
     */
    public void saveCollection(Collection<LabWork> collection) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(storagePath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            LabWorkList wrapper = new LabWorkList(collection);
            m.marshal(wrapper, bos);
            System.out.println("Коллекция сохранена в storage.xml.");
        } catch (JAXBException | IOException e) {
            System.err.println("Ошибка сохранения коллекции: " + e.getMessage());
        }
    }

    /**
     * Очищает файл storage.xml через буферизированный поток (удаляет все данные).
     */
    public void clearStorage() {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(storagePath,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            // просто очищаем файл
            System.out.println("storage.xml очищен.");
        } catch (IOException e) {
            System.err.println("Не удалось очистить storage.xml: " + e.getMessage());
        }
    }

    public Path getStoragePath() {
        return storagePath;
    }
}
