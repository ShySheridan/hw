package server_main;

import common_main.LabWork;
import common_main.LabWorkList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger log = LogManager.getLogger(StorageManager.class);

    public StorageManager(Path storageFilePath, Path inputFilePath) {
        this.storagePath = storageFilePath;
        this.inputPath   = inputFilePath;
        this.storageFile = storagePath.toFile();
        this.inputFile   = inputPath.toFile();
        try {
            this.jaxbContext = JAXBContext.newInstance(LabWorkList.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Не удалось инициализировать JAXBContext", e);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(storagePath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подготовить storage.xml: " + e.getMessage(), e);
        }

        loadFromInputFile();
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
            log.info("Коллекция сохранена в {}", storagePath);
        } catch (JAXBException | IOException e) {
            log.error("Ошибка сохранения в {}: {}", storagePath, e.toString());
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
            log.info("storage.xml очищен.");
        } catch (IOException e) {
            log.error("Не удалось очистить storage.xml: {}", e.getMessage());
        }
    }

    public Path getStoragePath() {
        return storagePath;
    }
}
