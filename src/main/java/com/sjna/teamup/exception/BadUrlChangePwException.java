package com.sjna.teamup.exception;

public class BadUrlChangePwException extends RuntimeException {

    public BadUrlChangePwException() {
        super();
    }

    public BadUrlChangePwException(String message) {
        super(message);
    }

    public BadUrlChangePwException(Throwable cause) {
        super(cause);
    }

    public BadUrlChangePwException(String message, Throwable cause) {
        super(message, cause);
    }

}
