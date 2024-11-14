package jp.kurashina.commons.exception;

import java.io.Serial;

public class AlreadyRegisteredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -677442940545667886L;

    public AlreadyRegisteredException() {
        super();
    }

    public AlreadyRegisteredException(String message) {
        super(message);
    }

}