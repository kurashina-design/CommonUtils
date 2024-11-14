package jp.kurashina.commons.exception;

import java.io.Serial;

public class CommandNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3785785314288365458L;

    public CommandNotFoundException() {
        super();
    }

    public CommandNotFoundException(String message) {
        super(message);
    }
}
