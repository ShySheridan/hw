# Клиентская часть Lab5

**Общая идея работы клиента**
Клиентская часть отвечает за приём команд пользователя через консоль, формирование объектов запроса (`server_main.Request`), их сериализацию в XML и передачу на сервер, а затем — чтение и отображение ответа (`server_main.Response`). Клиент **не** взаимодействует с внутренней коллекцией `server_main.LabWork` напрямую: все операции выполняются на стороне сервера.

## Состав пакета

- **server_main.ClientMain** (`Lab5.client.src.server_main.ClientMain`)
    - Точка входа приложения. Запускает цикл обработки команд.
- **server_main.ClientHandler** (`Lab5.client.src.server_main.ClientHandler`)
    - Основная логика: чтение ввода пользователя, разбор команды и аргументов, создание `server_main.Request`, отправка и получение `server_main.Response`.
- **server_main.ClientConnection** (`Lab5.client.src.server_main.ClientConnection`)
    - Транспортный слой на основе файловой системы:
        - `sendRequest(server_main.Request)` → запись `requests.xml` через JAXB-маршаллер
        - `readResponseFromXml()` → чтение `responses.xml` через JAXB-анмаршаллер

## Конфигурация путей

По умолчанию используется рабочая директория:
```text
requests.xml    – файл для outgoing-запросов
responses.xml   – файл для incoming-ответов
```

## Немного о JAXB (маршалинг/демаршалинг)

- **Маршалинг** (Java → XML): при `sendRequest` создаётся `JAXBContext` для класса `server_main.Request`, затем `Marshaller` преобразует объект в читаемый XML.
- **Анмаршалинг** (XML → Java): в `readResponseFromXml()` создаётся `Unmarshaller` для класса `server_main.Response`, который восстанавливает объект из готового XML.

---
