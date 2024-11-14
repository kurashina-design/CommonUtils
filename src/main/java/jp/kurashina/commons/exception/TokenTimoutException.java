package jp.kurashina.commons.exception;

import java.io.Serial;

public class TokenTimoutException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -774137020378705322L;

    public TokenTimoutException() {
    }

    public TokenTimoutException(String message) {
        super(message);
    }

}
