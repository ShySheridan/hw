package Lab5.common.src.enums;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "DifficultyEnum")
@XmlEnum
public enum Difficulty {
    @XmlEnumValue("EASY") EASY,
    @XmlEnumValue("HARD") HARD,
    @XmlEnumValue("IMPOSSIBLE") IMPOSSIBLE;

    public static boolean isValidDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            return false;
        }
        for (Difficulty d : Difficulty.values()) {
            if (d == difficulty) { // Сравнение по ссылке, так как это enum
                return true;
            }
        }
        return false;

    }
}




