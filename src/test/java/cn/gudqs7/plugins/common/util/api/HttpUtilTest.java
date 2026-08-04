package cn.gudqs7.plugins.common.util.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpUtilTest {

    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/echo", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            writeResponse(exchange, 200, exchange.getRequestMethod() + ":" + body);
        });
        server.createContext("/bad-request", exchange -> writeResponse(exchange, 400, "invalid request"));
        server.createContext("/large", exchange -> writeResponse(exchange, 200, "12345"));
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(200);
                writeResponse(exchange, 200, "late response");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void sendsBodyAndReadsSuccessfulResponse() {
        assertEquals("POST:payload", HttpUtil.sendHttpWithBody(url("/echo"), "POST", "payload", Collections.emptyMap()));
    }

    @Test
    void returnsErrorResponseBody() {
        assertEquals("invalid request", HttpUtil.sendHttpWithBody(url("/bad-request"), "POST", null, Collections.emptyMap()));
    }

    @Test
    void timesOutWhileWaitingForResponse() {
        assertThrows(RuntimeException.class,
                () -> HttpUtil.sendHttpWithBody(url("/slow"), "GET", null, Collections.emptyMap(), 50));
    }

    @Test
    void rejectsResponseLargerThanConfiguredLimit() {
        assertThrows(RuntimeException.class,
                () -> HttpUtil.sendHttpWithBody(url("/large"), "GET", null, Collections.emptyMap(), 1_000, 4));
    }

    private String url(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    private static void writeResponse(com.sun.net.httpserver.HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
