package server_main.net;

import common_main.Request;
import common_main.Response;
import common_main.net.Serde;
import server_main.CommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class TcpServer {
    private static final Logger log = LogManager.getLogger(TcpServer.class);

    private final int port;
    private final CommandHandler handler;

    private ServerSocketChannel server;
    private Selector selector;

    private static final class Session {
        final SocketChannel ch;
        ByteBuffer readLen = ByteBuffer.allocate(4);
        ByteBuffer readBody = null;
        final Deque<ByteBuffer> writeQ = new ArrayDeque<>();
        Session(SocketChannel ch) { this.ch = ch; }
    }

    private final ConnectionAcceptor acceptor = new ConnectionAcceptor();
    private final RequestReader reader = new RequestReader();
    private final ResponseWriter writer = new ResponseWriter();
    private final CommandExecutor executor = new CommandExecutor();

    public TcpServer(int port, CommandHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        server.register(selector, SelectionKey.OP_ACCEPT);

        log.info("Server starting on port {}", port);

        while (server.isOpen()) {
            int ready = selector.select(200);
            if (ready > 0) log.debug("Selected {} key(s)", ready);

            AdminConsole.pollAndMaybeSave(handler);

            var iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                try {
                    if (key.isAcceptable()) acceptor.accept(key, selector);
                    if (key.isReadable())   reader.read(key, executor);
                    if (key.isWritable())   writer.flush(key);
                } catch (CancelledKeyException ignored) {
                    log.debug("Key cancelled");
                } catch (IOException e) {
                    log.warn("I/O error on key: {}", e.toString());
                    closeKey(key);
                }
            }
        }
    }

    private static void closeKey(SelectionKey key) {
        try { key.channel().close(); } catch (IOException ignored) {}
        key.cancel();
    }

    /** Приём подключений */
    private class ConnectionAcceptor {
        void accept(SelectionKey key, Selector selector) throws IOException {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel ch = ssc.accept();
            if (ch == null) return;
            ch.configureBlocking(false);
            SelectionKey k = ch.register(selector, SelectionKey.OP_READ);
            k.attach(new Session(ch));
            try {
                log.info("Accepted connection from {}", ch.getRemoteAddress());
            } catch (IOException e) {
                log.info("Accepted connection (addr unavailable)");
            }
        }
    }

    /** Чтение запроса */
    private class RequestReader {
        void read(SelectionKey key, CommandExecutor exec) throws IOException {
            Session s = (Session) key.attachment();
            SocketChannel ch = (SocketChannel) key.channel();

            if (s.readBody == null) {
                int n = ch.read(s.readLen);
                if (n == -1) { log.debug("Client closed on length read"); throw new IOException("client closed"); }
                if (s.readLen.hasRemaining()) return;
                s.readLen.flip();
                int len = s.readLen.getInt();
                s.readBody = ByteBuffer.allocate(len);
                log.debug("Header read, body length={}", len);
            } else {
                int n = ch.read(s.readBody);
                if (n == -1) { log.debug("Client closed on body read"); throw new IOException("client closed"); }
                if (s.readBody.hasRemaining()) return;

                s.readBody.flip();
                byte[] bytes = new byte[s.readBody.remaining()];
                s.readBody.get(bytes);
                s.readLen.clear();
                s.readBody = null;

                exec.handle(bytes, s, key);
            }
        }
    }

    /** Обработка команд */
    private class CommandExecutor {
        void handle(byte[] reqBytes, Session s, SelectionKey key) {
            try {
                Request req = Serde.fromBytes(reqBytes);
                String cmd = String.valueOf(req.getCommandName());
                log.info("Received request: {}", cmd);

                if ("save".equalsIgnoreCase(cmd)) {
                    Response deny = Response.fail("Команда 'save' доступна только на сервере.");
                    writeResponse(deny, s, key);
                    log.warn("Denied remote 'save' command");
                    return;
                }

                Response resp = handler.handle(req);
                writeResponse(resp, s, key);
                log.info("Response queued for command: {}", cmd);
            } catch (Exception e) {
                log.error("Error handling request: {}", e.toString());
                try {
                    writeResponse(Response.fail("Ошибка обработки: " + e.getMessage()), s, key);
                } catch (IOException ignore) {}
            }
        }

        private void writeResponse(Response resp, Session s, SelectionKey key) throws IOException {
            byte[] out = Serde.toBytes(resp);
            ByteBuffer header = ByteBuffer.allocate(4).putInt(out.length);
            header.flip();
            s.writeQ.add(header);
            s.writeQ.add(ByteBuffer.wrap(out));
            key.interestOpsOr(SelectionKey.OP_WRITE);
        }
    }

    /** Отправка ответа */
    private static class ResponseWriter {
        private static final Logger log = LogManager.getLogger(ResponseWriter.class);
        void flush(SelectionKey key) throws IOException {
            Session s = (Session) key.attachment();
            SocketChannel ch = (SocketChannel) key.channel();
            while (!s.writeQ.isEmpty()) {
                ByteBuffer buf = s.writeQ.peek();
                int n = ch.write(buf);
                log.debug("Wrote {} bytes", n);
                if (buf.hasRemaining()) break;
                s.writeQ.poll();
            }
            if (s.writeQ.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    /** Серверная консоль: единственный способ выполнить 'save' */
    private static class AdminConsole {
        private static final Logger log = LogManager.getLogger(AdminConsole.class);
        private static final java.util.Scanner sc = new java.util.Scanner(System.in);
        static void pollAndMaybeSave(CommandHandler handler) {
            try {
                if (System.in.available() > 0) {
                    String line = sc.nextLine().trim().toLowerCase();
                    if ("save".equals(line)) {
                        handler.saveNow();
                        log.info("Collection saved via admin console");
                    }
                }
            } catch (IOException ignored) {}
        }
    }
}
