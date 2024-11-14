package jp.kurashina.commons.exception;

import java.io.Serial;

public class SystemErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5951306623513788132L;

    public SystemErrorException() {
        super();
    }

    public SystemErrorException(String message) {
        super(message);
    }

}
