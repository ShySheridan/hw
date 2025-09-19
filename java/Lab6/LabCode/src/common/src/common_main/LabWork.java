package common_main;


import common_main.enums.Difficulty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class LabWork implements Comparable<LabWork>, Serializable {

    private long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Float minimalPoint;
    private Difficulty difficulty;
    private Person author;

    public LabWork() {
    }

    public LabWork(String name, Coordinates coordinates, Float minimalPoint, Difficulty difficulty, Person author) {
        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.difficulty = difficulty;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    public void createNewId() {
        this.id = IdGenerator.generateUniqueId();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate() {
        creationDate = new java.util.Date();
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Float minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public int compareTo(LabWork o) {
        if (o == null) return 1;
        String a = this.name == null ? "" : this.name;
        String b = o.name == null ? "" : o.name;
        int byName = a.compareToIgnoreCase(b);
        if (byName != 0) return byName;
        return Long.compare(this.id, o.id);
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
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", difficulty=" + difficulty +
                ", author=" + author +
                '}';
    }
}
