package client_main;

public class ClientMain {
    public static void main(String[] args) {
        try (var connection = new ClientConnection()) {
            var handler = new   ClientHandler(connection);
            handler.start();
        } catch (Exception e) {
            System.err.println("Клиент завершён: " + e.getMessage());
        }
    }
}
