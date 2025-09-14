package src.common.src;

import src.common.src.enums.Color;
import src.common.src.enums.Country;

import java.io.Serializable;


public class Person implements Serializable {
    private String name;

    private Integer height;

    private Color eyeColor;

    private Color hairColor;

    private Country nationality;

    private Location location;

    public Person() {
    }

    public Person(String name, Integer height, Color eyeColor, Color hairColor, Country nationality, Location location) {
        this.name = name;
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        if (height == null || height <= 0)
            throw new IllegalArgumentException("Рост не может быть пустым или меньше нуля");
        this.height = height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(int index) {
        if (index >= Color.values().length || index < 0)
            throw new IllegalArgumentException("Нет цвета глаз по такому индексу");
        this.hairColor = eyeColor.values()[index];
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(int index) {
        if (index >= Color.values().length || index < 0)
            throw new IllegalArgumentException("Нет цвета волос по такому индексу");
        this.hairColor = hairColor.values()[index];
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(int index) {
//        if (!(isValidCountry(nationality))) throw new IllegalArgumentException("Неверное либо пустое значение");
        if (index >= Country.values().length || index < 0)
            throw new IllegalArgumentException("Нет национальности по такому индексу");
        this.nationality = Country.values()[index];
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) throw new IllegalArgumentException("Локация не может пыть пустой");
        this.location = location;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', height=" + height +
                ", eyeColor=" + eyeColor + ", hairColor=" + hairColor +
                ", nationality=" + nationality + ", location=" + location + '}';
    }

}
