package jp.kurashina.commons.exception;

import java.io.Serial;

public class ResourceInUseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -668973374943924179L;

    public ResourceInUseException() {
        super();
    }

    public ResourceInUseException(String message) {
        super(message);
    }

}