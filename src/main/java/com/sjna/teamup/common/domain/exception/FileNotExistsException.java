package com.sjna.teamup.common.domain.exception;

public class FileNotExistsException extends RuntimeException {

    public FileNotExistsException() {
        super();
    }

    public FileNotExistsException(String message) {
        super(message);
    }

    public FileNotExistsException(Throwable cause) {
        super(cause);
    }

    public FileNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
