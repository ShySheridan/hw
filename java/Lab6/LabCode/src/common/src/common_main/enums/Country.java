package common_main.enums;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

import java.io.Serializable;

@XmlType(name = "CountriesEnum")
@XmlEnum
public enum Country implements Serializable {
    @XmlEnumValue("SPAIN") SPAIN,
    @XmlEnumValue("INDIA") INDIA,
    @XmlEnumValue("THAILAND") THAILAND,
    @XmlEnumValue("NORTH_KOREA") NORTH_KOREA;

    public static boolean isValidCountry(Country country) {
        if (country == null) {
            return false;
        }
        for (Country c : Country.values()) {
            if (c == country) {
                return true;
            }
        }
        return false;
    }

//    public void print() {
//        countries = java.util.Arrays.toString(Country.values());
//
//    }

}
