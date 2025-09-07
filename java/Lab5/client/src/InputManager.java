package Lab5.client.src;

import Lab5.common.src.Coordinates;
import Lab5.common.src.LabWork;
import Lab5.common.src.Location;
import Lab5.common.src.Person;
import Lab5.common.src.enums.Color;
import Lab5.common.src.enums.Country;
import Lab5.common.src.enums.Difficulty;
import java.util.Scanner;

/**
 * Клиентская утилита для интерактивного ввода объекта LabWork, когда пользователь вводит команду в терминале
 */
public class InputManager {
    private final Scanner scanner;

    public InputManager(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Собирает LabWork, запрашивая поля у пользователя.
     */
    public LabWork buildLabWork() {
        LabWork lab = new LabWork();

        System.out.print("Введите предмет лабораторной работы: ");
        lab.setName(readNonEmptyString());

        System.out.println("Введите координаты:");
        Double x = readDouble("X");
        float y = readFloat("Y", value -> value > -763, "Значение Y должно быть больше -763");
        lab.setCoordinates(new Coordinates(x, y));

        Float minimalPoint = readFloat("Минимальный балл ", value -> value > 0, "Значение должно быть больше 0");
        lab.setMinimalPoint(minimalPoint);

        Difficulty difficulty = readEnum(Difficulty.class);
        lab.setDifficulty(difficulty);

        System.out.println("Введите данные автора:");
        Person author = readPerson();
        lab.setAuthor(author);

        return lab;
    }


    private String readNonEmptyString() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.print("Строка не может быть пустой. Повторите ввод: ");
        }
    }

    private Double readDouble(String filedName) {
        while (true) {
            System.out.print("Введите " + filedName + ": ");
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное вещественное число ");
            }
        }
    }


    private float readFloat(String fieldName, java.util.function.Predicate<Float> validator, String errorMessage) {
        while (true) {
            System.out.print("Введите " + fieldName + ": ");
            String input = scanner.nextLine().trim();
            try {
                float value = Float.parseFloat(input);
                if (validator.test(value)) return value;
                System.out.println(errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное вещественное число ");
            }
        }
    }

//    private Float readNullableFloat(String fieldName, java.util.function.Predicate<Float> validator, String errorMessage) {
//        while (true) {
//            System.out.print(fieldName + ": ");
//            String input = scanner.nextLine().trim();
//            if (input.isEmpty()) return null;
//            try {
//                float value = Float.parseFloat(input);
//                if (validator.test(value)) return value;
//                System.out.println(errorMessage);
//            } catch (NumberFormatException e) {
//                System.out.println("Введите корректное число (float).");
//            }
//        }
//    }

    /**
     * Универсальный метод для выбора значения из enum по номеру или по названию.
     *
     * @param enumClass класс перечисления
     * @param <T>       тип перечисления
     * @return выбранное значение enum
     */
    private <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();

        if (enumClass == null) {
            System.out.println("Поле не может быть пустым.");
//            continue; //TODO enumClass != null
            //TODO не сериализуется рост цвет глаз
        }

        // Выводим список со значениями и их порядковыми номерами
        System.out.println("Выберите значение (" + enumClass.getSimpleName() + "):");
        for (int i = 0; i < constants.length; i++) {
            System.out.printf("  %d) %s%n", i + 1, constants[i].name());
        }

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            // Если введена цифра, пытаемся интерпретировать её как индекс
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= constants.length) {
                    return constants[idx - 1];
                } else {
                    System.out.printf("Номер вне диапазона. Введите число от %d до %d.%n",
                            1, constants.length);
                    continue;
                }
            } catch (NumberFormatException ignored) {
                // Если не цифра, попробуем разобрать как имя enum'а
            }

            // Если введено имя, пытаемся найти соответствующий элемент
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.printf("Неверное значение. Допустимые номера: 1–%d, или одно из имён: %s%n",
                        constants.length,
                        String.join(", ",
                                java.util.Arrays.stream(constants)
                                        .map(Enum::name)
                                        .toArray(String[]::new))
                );
            }
        }
    }


    private Person readPerson() {
        System.out.print("Введите имя автора ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) return null;

        Integer height = readInteger("Рост", value -> value > 0, "Рост должен быть больше 0");
        Color eyeColor = readEnum(Color.class);
        Color hairColor = readEnum(Color.class);
        Country nationality = readEnum(Country.class);

        System.out.println("Введите локацию:");
        long x = readLong("X:");
        float y = readFloat("Y:", val -> true, "");
        double z = readDouble("Z:");

        Location location = new Location(x, y, z);
        return new Person(name, height, eyeColor, hairColor, nationality, location);
    }

    private Integer readInteger(String fieldName, java.util.function.Predicate<Integer> validator, String errorMessage) {
        while (true) {
            System.out.print("Введите " + fieldName + ": ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Поле не может быть пустым.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (!validator.test(value)) {
                    System.out.println(errorMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное целое число ");
            }
        }
    }

    private Long readLong(String fieldName) {
        while (true) {
            System.out.print("Введите " + fieldName + ": ");
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число ");
            }
        }
    }

}

