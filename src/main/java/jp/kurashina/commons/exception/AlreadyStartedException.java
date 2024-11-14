package jp.kurashina.commons.exception;

import java.io.Serial;

public class AlreadyStartedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 761043273199173086L;

    public AlreadyStartedException() {
        super();
    }

    public AlreadyStartedException(String message) {
        super(message);
    }

}
