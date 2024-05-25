package com.sjna.teamup.exception;

public class CreateFileFailureException extends RuntimeException {

    public CreateFileFailureException() {
        super();
    }

    public CreateFileFailureException(String message) {
        super(message);
    }

    public CreateFileFailureException(Throwable cause) {
        super(cause);
    }

    public CreateFileFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
