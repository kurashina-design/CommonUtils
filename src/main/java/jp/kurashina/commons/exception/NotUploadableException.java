package jp.kurashina.commons.exception;

import java.io.Serial;

public class NotUploadableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 472323678822989071L;

    public NotUploadableException() {
        super();
    }

    public NotUploadableException(String message) {
        super(message);
    }

}
