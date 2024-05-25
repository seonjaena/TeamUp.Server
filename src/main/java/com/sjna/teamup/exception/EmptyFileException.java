package com.sjna.teamup.exception;

public class EmptyFileException extends RuntimeException {

    public EmptyFileException() {
        super();
    }

    public EmptyFileException(String message) {
        super(message);
    }

    public EmptyFileException(Throwable cause) {
        super(cause);
    }

    public EmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
