package client_main;

import common_main.Request;
import common_main.Response;
import common_main.net.Serde;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class ClientConnection implements Closeable {
    private final String host;
    private final int port;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public ClientConnection() { this("127.0.0.1", 5555); }
    public ClientConnection(String host, int port) {
        this.host = host; this.port = port;
    }

    private void ensureConnected() throws IOException {
        if (socket != null && socket.isConnected() && !socket.isClosed()) return;
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 1500);
        socket.setTcpNoDelay(true);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
    }

    /**
     * Отправляет запрос и синхронно читает ответ. При временной недоступности —
     * делает несколько попыток с экспоненциальной задержкой.
     */
    public Response requestResponse(Request req) {
        int attempts = 0;
        int maxAttempts = 6; // ~ до ~10 сек
        long backoffMs = 250;

        while (true) {
            try {
                ensureConnected();
                send(req);
                return receive();
            } catch (IOException | ClassNotFoundException e) {
                attempts++;
                closeQuietly();
                if (attempts >= maxAttempts) {
                    return new Response(false,
                            "Сервер временно недоступен. Попробуйте позже. Тех.детали: " + e.getMessage());
                }
                try { TimeUnit.MILLISECONDS.sleep(backoffMs); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new Response(false, "Ожидание прервано.");
                }
                backoffMs = Math.min(backoffMs * 2, 2000);
            }
        }
    }

    private void send(Request req) throws IOException {
        byte[] payload = Serde.toBytes(req);
        byte[] header = ByteBuffer.allocate(4).putInt(payload.length).array();
        out.write(header);
        out.write(payload);
        out.flush();
    }

    private Response receive() throws IOException, ClassNotFoundException {
        byte[] header = in.readNBytes(4);
        if (header.length < 4) throw new EOFException("Канал закрыт");
        int len = ByteBuffer.wrap(header).getInt();
        byte[] body = in.readNBytes(len);
        if (body.length < len) throw new EOFException("Не получили полный ответ");
        return Serde.fromBytes(body);
    }

    private void closeQuietly() {
        try { close(); } catch (IOException ignored) {}
    }

    @Override public void close() throws IOException {
        if (socket != null) socket.close();
        socket = null; in = null; out = null;
    }
}
