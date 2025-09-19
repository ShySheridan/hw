# Lab5 Common Module (Lab5.common.src)

**Модуль общего использования** между клиентом и сервером. Тут лежат все сущности (server_main.LabWork, server_main.Person, server_main.Coordinates, перечисления), а также классы обмена (server_main.Request, server_main.Response) и обёртки для XML

## Содержание пакета

- **Доменные классы**:
    - `server_main.LabWork` — основная сущность лабораторной работы (поля: `id`, `name`, `coordinates`, `creationDate`, `minimalPoint`, `difficulty`, `author`). Реализует `Comparable<server_main.LabWork>` по имени и содержит валидацию полей.
    - `server_main.Coordinates` — координаты (`x: Double`, `y: float`) с проверками диапазонов. 
    - `server_main.Person` — автор работы (`name`, `height`, `eyeColor`, `hairColor`, `nationality`, `location`) с валидацией. 
    - `server_main.Location` — локация автора (`x: long`, `y: float`, `z: double`) 

- **Утилиты**:
    - `server_main.IdGenerator` — гарантирует уникальность `id` через `generateUniqueId()` и позволяет загружать уже использованные ID. 

- **Классы-обёртки для JAXB**:
    - `LabWorkList` — контейнер для списка `server_main.LabWork` при (де)сериализации XML. 
    - `server_main.Request` — запрос от клиента к серверу: имя команды (`commandName`) и аргументы (`String[] arguments`). Содержит метод `toXmlFile()` для записи в XML. 
    - `server_main.Response` — ответ сервера клиенту: флаг успеха (`success`) и сообщение (`message`), также умеет сериализовать себя в XML методом `toXmlFile()`. 

## Назначение

Этот модуль:

1. **Обеспечивает единое представление данных** — обе стороны (клиент и сервер) работают с одним набором классов.
2. **Инкапсулирует бизнес‑модель** (`server_main.LabWork`, `server_main.Person` и т.п.) и логику валидации.
3. **Упрощает (де)сериализацию XML** через JAXB-аннотации.

---


