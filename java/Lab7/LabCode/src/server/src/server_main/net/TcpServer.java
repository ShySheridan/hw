package server_main.net;

import common_main.net.Serde;
import server_main.CommandHandler;
import common_main.Request;
import common_main.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class TcpServer {
    private static final Logger log = LogManager.getLogger(TcpServer.class);

    private final int port;
    private final CommandHandler handler;

    private ServerSocketChannel server;
    private Selector selector;

    private final ForkJoinPool readPool = new ForkJoinPool();
    private final ForkJoinPool processPool = new ForkJoinPool();
    private final ExecutorService writePool = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2));

    private static final class Session {
        final SocketChannel ch;
        final Object writeLock = new Object();
        ByteBuffer readLen = ByteBuffer.allocate(4);
        ByteBuffer readBody = null;
        Session(SocketChannel ch) { this.ch = ch; }
    }

    public TcpServer(int port, CommandHandler handler) {
        this.port = port; this.handler = handler;
    }

    public void start() throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        server.register(selector, SelectionKey.OP_ACCEPT);
        log.info("Server starting on port {}", port);

        while (server.isOpen()) {
            selector.select(200);
            var it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next(); it.remove();
                try {
                    if (key.isAcceptable()) accept(key);
                    if (key.isReadable())   read(key);
                } catch (CancelledKeyException ignored) {
                } catch (IOException e) {
                    closeKey(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel ch = ssc.accept();
        if (ch == null) return;
        ch.configureBlocking(false);
        SelectionKey k = ch.register(selector, SelectionKey.OP_READ);
        k.attach(new Session(ch));
        log.info("Accepted {}", ch.getRemoteAddress());
    }

    private void read(SelectionKey key) throws IOException {
        Session s = (Session) key.attachment();
        SocketChannel ch = (SocketChannel) key.channel();

        if (s.readBody == null) {
            int n = ch.read(s.readLen);
            if (n == -1) throw new IOException("client closed");
            if (s.readLen.hasRemaining()) return;
            s.readLen.flip();
            int len = s.readLen.getInt();
            s.readBody = ByteBuffer.allocate(len);
        } else {
            int n = ch.read(s.readBody);
            if (n == -1) throw new IOException("client closed");
            if (s.readBody.hasRemaining()) return;

            s.readBody.flip();
            byte[] bytes = new byte[s.readBody.remaining()];
            s.readBody.get(bytes);
            s.readLen.clear();
            s.readBody = null;

            readPool.execute(() -> handleRequest(bytes, s));
        }
    }

    private void handleRequest(byte[] reqBytes, Session s) {
        try {
            Request req = Serde.fromBytes(reqBytes);
            String cmd = String.valueOf(req.getCommandName());
            if ("save".equalsIgnoreCase(cmd)) {
                Response deny = Response.fail("Команда 'save' доступна только на сервере.");
                enqueueWrite(deny, s);
                return;
            }
            processPool.execute(() -> {
                Response resp = handler.handle(req);
                enqueueWrite(resp, s);
            });
        } catch (Exception e) {
            enqueueWrite(Response.fail("Ошибка обработки: " + e.getMessage()), s);
        }
    }

    private void enqueueWrite(Response resp, Session s) {
        writePool.execute(() -> {
            try {
                byte[] out = Serde.toBytes(resp);
                ByteBuffer hdr = ByteBuffer.allocate(4).putInt(out.length);
                hdr.flip();
                ByteBuffer body = ByteBuffer.wrap(out);
                synchronized (s.writeLock) {
                    while (hdr.hasRemaining()) s.ch.write(hdr);
                    while (body.hasRemaining()) s.ch.write(body);
                }
                log.debug("main.Response sent ({} bytes)", out.length);
            } catch (IOException e) {
                log.warn("Write failed: {}", e.toString());
                try { s.ch.close(); } catch (IOException ignored) {}
            }
        });
    }

    private static void closeKey(SelectionKey key) {
        try { key.channel().close(); } catch (IOException ignored) {}
        key.cancel();
    }
}
