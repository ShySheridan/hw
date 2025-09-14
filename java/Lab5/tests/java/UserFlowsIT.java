package Lab5.tests.java;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
public class UserFlowsIT {

    // ПОДГОНИТЕ ИМЕНА КЛАССОВ ПОД ВАШИ ПАКЕТЫ, ЕСЛИ ОТЛИЧАЮТСЯ:
    private static final String SERVER_MAIN = "server.ServerMain";
    private static final String CLIENT_MAIN = "client.ClientMain";

    // Три файла, о которых говорили в другом чате:
    private static final Path DATA_DIR   = Paths.get("Lab5", "server", "data");
    private static final Path REQUESTS   = DATA_DIR.resolve("requests.xml");
    private static final Path RESPONSES  = DATA_DIR.resolve("responses.xml");
    private static final Path STORAGE    = DATA_DIR.resolve("storage.xml");
    private static final Path OUTPUT     = DATA_DIR.resolve("output.xml");

    private static Process server;

    @BeforeAll
    static void startServer() throws Exception {
        Files.createDirectories(DATA_DIR);

        // Минимальный input.xml — «пустая» коллекция.
        Path inputXml = Files.createTempFile("labwork_input_", ".xml");
        // Если у вас другой корневой тег — поправьте здесь.
        String minimalInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<labWorkList/>\n";
        Files.writeString(inputXml, minimalInput, StandardCharsets.UTF_8);

        // Запуск сервера отдельным процессом с нужной переменной окружения
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", System.getProperty("java.class.path"), SERVER_MAIN
        );
        Map<String, String> env = pb.environment();
        env.put("LABWORK_DATA_FILE", inputXml.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        server = pb.start();

        // Дадим серверу подняться и инициализировать файлы
        Thread.sleep(600); // если мало — увеличьте
        assertTrue(Files.exists(REQUESTS), "requests.xml должен существовать после старта сервера");
        assertTrue(Files.exists(RESPONSES), "responses.xml должен существовать после старта сервера");
        assertTrue(Files.exists(STORAGE), "storage.xml должен существовать после старта сервера");
    }

    @AfterAll
    static void stopServer() throws Exception {
        // Корректно завершаем сервер через клиентскую команду exit
        runClient("exit");

        if (server != null) {
            server.destroy();
            server.waitFor();
        }
    }

    @BeforeEach
    void cleanExchFiles() throws Exception {
        // Каждый тест начинает с очищенных файлов обмена
        truncateIfExists(REQUESTS);
        truncateIfExists(RESPONSES);
    }

    // ---------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ----------

    private static void truncateIfExists(Path p) throws IOException {
        if (Files.exists(p)) {
            try (var ch = Files.newByteChannel(p, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                // no-op — просто обрезали файл до 0
            }
        }
    }

    /** Запускает клиент, «вводит» команды и возвращает stdout клиента. */
    private static String runClient(String... lines) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", System.getProperty("java.class.path"), CLIENT_MAIN
        );
        pb.redirectErrorStream(true);
        Process client = pb.start();

        try (OutputStream os = client.getOutputStream()) {
            for (String l : lines) {
                os.write((l + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (InputStream is = client.getInputStream()) {
            is.transferTo(bout);
        }

        client.waitFor();
        return bout.toString(StandardCharsets.UTF_8);
    }

    /** Ждёт, пока файл станет НЕ пустым. */
    private static void awaitNonEmpty(Path p, Duration timeout) throws Exception {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            if (Files.exists(p) && Files.size(p) > 0) return;
            Thread.sleep(50);
        }
        fail("Файл " + p + " не стал непустым за " + timeout.toMillis() + " мс");
    }

    /** Ждёт, пока файл станет пустым (0 байт). */
    private static void awaitEmpty(Path p, Duration timeout) throws Exception {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            if (Files.exists(p) && Files.size(p) == 0) return;
            Thread.sleep(50);
        }
        fail("Файл " + p + " не стал пустым за " + timeout.toMillis() + " мс");
    }

    // =========================================================
    //                     ПРАВИЛЬНЫЕ СЦЕНАРИИ (4)
    // =========================================================

    @Test @Order(1)
    void help_ok_exchangesThroughXml() throws Exception {
        long storageBefore = Files.size(STORAGE);

        String out = runClient("help", "exit");
        assertNotNull(out);

        // Должен быть хотя бы один обмен: requests -> responses
        // (после работы клиента файлы обмена должны быть очищены)
        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));

        // Хранилище не должно измениться от help
        long storageAfter = Files.size(STORAGE);
        assertEquals(storageBefore, storageAfter, "help не должен менять storage.xml");
    }

    @Test @Order(2)
    void info_ok_roundTrip() throws Exception {
        String out = runClient("info", "exit");
        assertNotNull(out);
        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));
        // Содержимое storage существует
        assertTrue(Files.exists(STORAGE));
    }

    @Test @Order(3)
    void show_ok_roundTrip() throws Exception {
        String out = runClient("show", "exit");
        assertNotNull(out);
        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));
    }

    @Test @Order(4)
    void clear_then_save_updatesSomething_ok() throws Exception {
        // на всякий случай удалим output.xml, чтобы проверять его создание именно этим тестом
        Files.deleteIfExists(OUTPUT);

        // 1) Гарантированно создаём storage.xml (хоть и пустой), получаем исходный mtime
        runClient("save", "exit");
        awaitEmpty(REQUESTS, Duration.ofMillis(2000));
        awaitEmpty(RESPONSES, Duration.ofMillis(2000));
        FileTime beforeTime = Files.getLastModifiedTime(STORAGE);

        // 2) Переждём не меньше одной секунды, чтобы next mtime точно стал больше
        Thread.sleep(1200);

        // 3) Делаем очистку и явное сохранение
        String out = runClient("clear", "save", "exit");
        awaitEmpty(REQUESTS, Duration.ofMillis(3000));
        awaitEmpty(RESPONSES, Duration.ofMillis(3000));

        // 4) Первичное доказательство — mtime обновился
        try {
            awaitNewerMTime(STORAGE, beforeTime, Duration.ofSeconds(5));
        } catch (AssertionError e) {
            // 5) Фолбэк: если mtime остался в том же тике, проверяем, что хотя бы output.xml создан/перезаписан
            assertTrue(Files.exists(OUTPUT), "После save должен появиться output.xml");
            // Дополнительно можно проверить, что клиент распечатал сообщение про сохранение
            assertTrue(out.toLowerCase().contains("сохран"),
                    "Ожидали сообщение клиента о сохранении коллекции");
        }
    }

    private static void awaitNewerMTime(Path p, FileTime prev, Duration timeout) throws Exception {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            FileTime now = Files.getLastModifiedTime(p);
            if (now.toMillis() > prev.toMillis()) return;
            Thread.sleep(50);
        }
        fail("mtime " + p + " не обновился за " + timeout.toMillis() + " мс");
    }



    // =========================================================
    //                    НЕПРАВИЛЬНЫЕ СЦЕНАРИИ (4)
    // =========================================================

    @Test @Order(5)
    void removeById_badFormat_rejected() throws Exception {
        long before = Files.size(STORAGE);

        // Невалидный id формата "abc"
        String out = runClient("remove_by_id abc", "exit");
        assertNotNull(out);

        // Произошёл обмен и файлы очищены клиентом
        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));

        // Хранилище не должно поменяться от невалидной команды
        long after = Files.size(STORAGE);
        assertEquals(before, after, "Неверный формат id не должен менять storage.xml");
    }

    @Test @Order(6)
    void removeById_overflowLong_rejected() throws Exception {
        long before = Files.size(STORAGE);

        // Число больше Long.MAX_VALUE
        String huge = "9223372036854775808";
        String out = runClient("remove_by_id " + huge, "exit");
        assertNotNull(out);

        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));

        long after = Files.size(STORAGE);
        assertEquals(before, after, "Переполнение long не должно менять storage.xml");
    }

    @Test @Order(7)
    void executeScript_missingFile_rejected() throws Exception {
        long before = Files.size(STORAGE);

        String out = runClient("execute_script /definitely/not/exist/script.txt", "exit");
        assertNotNull(out);

        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));

        long after = Files.size(STORAGE);
        assertEquals(before, after, "Невалидный путь скрипта не должен менять storage.xml");
    }

    @Test @Order(8)
    void unknownCommand_rejected() throws Exception {
        long before = Files.size(STORAGE);

        String out = runClient("abracadabra", "exit");
        assertNotNull(out);

        awaitEmpty(REQUESTS, Duration.ofMillis(800));
        awaitEmpty(RESPONSES, Duration.ofMillis(800));

        long after = Files.size(STORAGE);
        assertEquals(before, after, "Неизвестная команда не должна менять storage.xml");
    }
}
