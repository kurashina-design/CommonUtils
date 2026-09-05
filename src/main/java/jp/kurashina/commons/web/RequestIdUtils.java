package jp.kurashina.commons.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class RequestIdUtils {

    public static final String HEADER_NAME = "X-Request-Id";
    public static final String ATTRIBUTE_NAME = RequestIdUtils.class.getName() + ".requestId";

    private RequestIdUtils() {
    }

    public static Optional<String> get(HttpServletRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        Object attribute = request.getAttribute(ATTRIBUTE_NAME);
        if (attribute instanceof String value && !value.isBlank()) {
            return Optional.of(value);
        }
        String header = request.getHeader(HEADER_NAME);
        return header == null || header.isBlank() ? Optional.empty() : Optional.of(header);
    }

    public static String getOrCreate(HttpServletRequest request) {
        String requestId = get(request).orElseGet(() -> UUID.randomUUID().toString());
        request.setAttribute(ATTRIBUTE_NAME, requestId);
        return requestId;
    }
}
