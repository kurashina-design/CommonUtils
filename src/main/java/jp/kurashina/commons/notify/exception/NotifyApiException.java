package jp.kurashina.commons.notify.exception;

import java.io.Serial;

public class NotifyApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7314690991141328428L;

    private final Integer statusCode;
    private final String responseBody;

    public NotifyApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
        this.responseBody = null;
    }

    public NotifyApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
