package common_main.enums;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlType(name = "ColorEnum")
@XmlEnum
public enum Color implements Serializable {
    @XmlEnumValue("BLACK") BLACK,
    @XmlEnumValue("ORANGE") ORANGE,
    @XmlEnumValue("BROWN") BROWN,
    @XmlEnumValue("GREEN") GREEN,
    @XmlEnumValue("RED") RED,
    @XmlEnumValue("YELLOW") YELLOW;

    public static boolean isValidColor(Color color) {
        if (color == null) {
            return false;
        }
        for (Color c : Color.values()) {
            if (c == color) {
                return true;
            }
        }
        //new IllegalArgumentException("Ошибка: '" + color + "' не найдено в Color.");
        return false;
    }
}
