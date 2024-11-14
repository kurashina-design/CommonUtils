package jp.kurashina.commons.exception;

import java.io.Serial;

public class MalFormedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 39379839175281084L;

    public MalFormedException() {
    }

    public MalFormedException(String message) {
        super(message);
    }
}
