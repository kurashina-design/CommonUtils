package jp.kurashina.commons.exception;

import java.io.Serial;

public class DependencyException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7988223333741334052L;

    public DependencyException() {
        super();
    }

    public DependencyException(String message) {
        super(message);
    }

}
