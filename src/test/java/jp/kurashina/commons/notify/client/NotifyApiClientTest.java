package jp.kurashina.commons.notify.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jp.kurashina.commons.notify.dto.NotifyEmailRequest;
import jp.kurashina.commons.notify.dto.NotifyEmailResponse;
import jp.kurashina.commons.notify.exception.NotifyApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotifyApiClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsBearerTokenAndParsesAcceptedResponse() throws Exception {
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<JsonNode> requestBody = new AtomicReference<>();
        startServer(exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestBody.set(objectMapper.readTree(exchange.getRequestBody()));
            respond(exchange, 202, "{\"messageId\":\"message-1\",\"smtpServer\":\"smtp-1\"}");
        });

        NotifyEmailRequest request = new NotifyEmailRequest();
        request.setSubject("Subject");

        NotifyEmailResponse response = client().sendEmail("firebase-token", request);

        assertEquals("Bearer firebase-token", authorization.get());
        assertEquals("Subject", requestBody.get().get("subject").asText());
        assertEquals("message-1", response.getMessageId());
        assertEquals("smtp-1", response.getSmtpServer());
    }

    @Test
    void exposesUnexpectedStatusAndResponseBody() throws Exception {
        startServer(exchange -> respond(exchange, 403, "{\"message\":\"forbidden\"}"));

        NotifyApiException exception = assertThrows(
                NotifyApiException.class,
                () -> client().sendEmail("firebase-token", new NotifyEmailRequest()));

        assertEquals(403, exception.getStatusCode());
        assertEquals("{\"message\":\"forbidden\"}", exception.getResponseBody());
    }

    private NotifyApiClient client() {
        return new NotifyApiClient("http://localhost:" + server.getAddress().getPort());
    }

    private void startServer(ExchangeHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/v1/emails", exchange -> handler.handle(exchange));
        server.start();
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    @FunctionalInterface
    private interface ExchangeHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
