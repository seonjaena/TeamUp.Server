package com.sjna.teamup.common.domain.exception;

public class BadFileExtensionException extends RuntimeException {

    public BadFileExtensionException() {
        super();
    }

    public BadFileExtensionException(String message) {
        super(message);
    }

    public BadFileExtensionException(Throwable cause) {
        super(cause);
    }

    public BadFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

}
