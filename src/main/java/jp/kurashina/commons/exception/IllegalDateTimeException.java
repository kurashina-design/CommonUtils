package jp.kurashina.commons.exception;

import java.io.Serial;

public class IllegalDateTimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5013779848854471796L;

    public IllegalDateTimeException() {
        super();
    }

    public IllegalDateTimeException(String message) {
        super(message);
    }
}
