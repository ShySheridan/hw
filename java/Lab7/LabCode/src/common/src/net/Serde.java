package src.common.src.net;

import java.io.*;

/**
 * Утилитный класс для (де)сериализации объектов в/из массива байт.
 * <p>
 * Используется для кадрирования сообщений при обмене по TCP (NIO) и для
 * хранения доменных объектов в БД в виде {@code bytea}. Каждый вызов
 * создаёт новый объектный поток, поэтому получаем независимые «кадры»
 * без побочных эффектов кэширования заголовков {@link ObjectOutputStream}.
 * <p>
 * Класс статичен и не содержит состояния; создавать экземпляры не требуется.
 *
 * @see Serializable
 * @see ObjectOutputStream
 * @see ObjectInputStream
 */
public final class Serde {
    private Serde() {
    }

    public static byte[] toBytes(Serializable obj) throws IOException {
        try (var baos = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует объект из массива байт, ранее полученного через {@link #toBytes(Serializable)}.
     * <p>
     * Обратите внимание: метод выполняет небезопасное приведение типа, поэтому
     * ответственность за корректность ожидаемого типа лежит на вызывающем коде.
     *
     * @param data массив байт с сериализованным объектом
     * @param <T> ожидаемый тип результата
     * @return восстановленный объект
     * @throws IOException при ошибке чтения/десериализации
     * @throws ClassNotFoundException если класс объекта отсутствует в classpath
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        try (var bais = new ByteArrayInputStream(data); var ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        }
    }
}
