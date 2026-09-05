package jp.kurashina.commons.api.error;

import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ApiErrorResponseFactory {

    public static final String INTERNAL_SERVER_ERROR_DETAIL = "Internal server error occurred.";

    private static final Set<String> RESERVED_DATA_KEYS = Set.of(
            "statusCode", "statusMessage", "detail", "requestId", "errors"
    );

    private ApiErrorResponseFactory() {
    }

    public static ApiErrorResponse create(HttpStatusCode status, String detail, String requestId) {
        return create(status, detail, requestId, null, null);
    }

    public static ApiErrorResponse create(HttpStatusCode status, String detail, String requestId,
                                          List<ApiErrorDetail> errors, Map<String, Object> data) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        validateData(data);
        String safeDetail = status.is5xxServerError() ? INTERNAL_SERVER_ERROR_DETAIL : detail;
        return new ApiErrorResponse(status.value(), safeDetail, requestId, errors, data);
    }

    private static void validateData(Map<String, Object> data) {
        if (data == null) {
            return;
        }
        for (String key : RESERVED_DATA_KEYS) {
            if (data.containsKey(key)) {
                throw new IllegalArgumentException("data must not contain reserved key: " + key);
            }
        }
    }
}
