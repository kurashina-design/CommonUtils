package jp.kurashina.commons.exception;

import java.io.Serial;

public class NotAuthorizedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6479124096494790838L;

    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String message) {
        super(message);
    }
}
