package Lab5.common.src;

import Lab5.common.src.enums.Difficulty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;


/**
 * Основной класс лабы.
 *
 * <p>Инварианты:
 * <ul>
 *   <li>{@code id > 0} уникальный айди в коллекции.</li>
 *   <li>{@code name} не {@code null} и не пустой.</li>
 *   <li>Сортировка по умолчанию — лексикографически по {@code name}.</li>
 * </ul>
 *
 * <p>Сериализуется/десериализуется через JAXB в составе {@link LabWorkList}.
 * @see LabWorkList
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LabWork implements Comparable<LabWork> {
    private long id;
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private Coordinates coordinates;
    private java.util.Date creationDate;
    @XmlElement(required = true)
    private Float minimalPoint;
    @XmlElement(required = true)
    private Difficulty difficulty;
    @XmlElement(required = true)
    private Person author;
//    public static HashSet<Long> idSet = new HashSet<>();

    /**
     * Default constructor
     */
    public LabWork() {
        //id = createNewId();
        //creationDate = new java.util.Date();
    }
    public LabWork(String name,  Coordinates coordinates, Float minimalPoint, Difficulty difficulty, Person author) {
        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.difficulty = difficulty;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Координаты не могут быть null!");
        this.coordinates = coordinates;
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Float minimalPoint) {
        if (minimalPoint != null && minimalPoint <= 0)
            throw new IllegalArgumentException("Минимальная оценка должна быть > 0!");
        this.minimalPoint = minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) throw new IllegalArgumentException("Сложность не может быть пустой");
//        if (!(isValidDifficulty(difficulty))) throw new IllegalArgumentException("Введите другую сложность")
        this.difficulty = difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public void createNewId() {
        this.id = IdGenerator.generateUniqueId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public java.util.Date setCreationDate() {
        creationDate = new java.util.Date();
        return creationDate;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabWork labWork = (LabWork) o;
        return id == labWork.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", difficulty=" + difficulty +
                ", author=" + author +
                '}';
    }

    /**
     * Сортировка по названию лабораторных работ
     *
     * @param other другой экземпляр {@code LabWork}
     * @return отрицательное/нулевое/положительное значение по контракту {@link Comparable}
     * @throws NullPointerException если {@code other} или его {@code name} равны {@code null}
     */
    @Override
    public int compareTo(LabWork other) {
        return this.name.compareToIgnoreCase(other.name); //
    }
// :TODO: isEmpty ?
//    public boolean isEmpty() {
//        return false;
//    }
}



