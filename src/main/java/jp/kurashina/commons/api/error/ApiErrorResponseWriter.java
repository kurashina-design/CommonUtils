package jp.kurashina.commons.api.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jp.kurashina.commons.web.RequestIdUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ApiErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ApiErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public void write(HttpServletResponse response, ApiErrorResponse body) throws IOException {
        Objects.requireNonNull(response, "response must not be null");
        Objects.requireNonNull(body, "body must not be null");

        response.setStatus(body.getStatusCode());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        if (body.getRequestId() != null) {
            response.setHeader(RequestIdUtils.HEADER_NAME, body.getRequestId());
        }
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
