package jp.kurashina.commons.notify.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.kurashina.commons.notify.dto.NotifyEmailRequest;
import jp.kurashina.commons.notify.dto.NotifyEmailResponse;
import jp.kurashina.commons.notify.exception.NotifyApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class NotifyApiClient {

    private static final String EMAILS_PATH = "/v1/emails";

    private final URI emailsUri;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NotifyApiClient(String baseUrl) {
        this(baseUrl, HttpClient.newHttpClient(), new ObjectMapper());
    }

    public NotifyApiClient(String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.emailsUri = buildEmailsUri(baseUrl);
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public NotifyEmailResponse sendEmail(String firebaseIdToken, NotifyEmailRequest emailRequest) {
        if (firebaseIdToken == null || firebaseIdToken.isBlank()) {
            throw new IllegalArgumentException("firebaseIdToken must not be blank");
        }
        Objects.requireNonNull(emailRequest, "emailRequest must not be null");

        HttpRequest request = HttpRequest.newBuilder(emailsUri)
                .header("Authorization", "Bearer " + firebaseIdToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(writeRequest(emailRequest)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 202) {
                throw new NotifyApiException(
                        "notify-api returned unexpected HTTP status " + response.statusCode(),
                        response.statusCode(),
                        response.body());
            }
            return readResponse(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new NotifyApiException("notify-api request was interrupted", exception);
        } catch (IOException exception) {
            throw new NotifyApiException("notify-api request failed", exception);
        }
    }

    private String writeRequest(NotifyEmailRequest emailRequest) {
        try {
            return objectMapper.writeValueAsString(emailRequest);
        } catch (JsonProcessingException exception) {
            throw new NotifyApiException("Failed to serialize notify-api email request", exception);
        }
    }

    private NotifyEmailResponse readResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return new NotifyEmailResponse();
        }
        try {
            return objectMapper.readValue(responseBody, NotifyEmailResponse.class);
        } catch (JsonProcessingException exception) {
            throw new NotifyApiException("Failed to deserialize notify-api email response", exception);
        }
    }

    private static URI buildEmailsUri(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        return URI.create(normalizedBaseUrl + EMAILS_PATH);
    }
}
