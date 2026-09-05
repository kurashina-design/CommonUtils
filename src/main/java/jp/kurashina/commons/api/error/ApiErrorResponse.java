package jp.kurashina.commons.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ApiErrorResponse {

    private final int statusCode;
    private final String statusMessage;
    private final String detail;
    private final String requestId;
    private final List<ApiErrorDetail> errors;
    private final Map<String, Object> data;

    public ApiErrorResponse(int statusCode, String detail, String requestId,
                            List<ApiErrorDetail> errors, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.statusMessage = deriveStatusMessage(statusCode);
        this.detail = Objects.requireNonNull(detail, "detail must not be null");
        this.requestId = normalizeOptional(requestId);
        this.errors = errors == null ? null : List.copyOf(errors);
        this.data = data == null ? null : Map.copyOf(data);
    }

    private static String deriveStatusMessage(int statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode);
        return status == null ? "Unknown Status" : status.getReasonPhrase();
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
