package com.sjna.teamup.exception;

public class DecryptionException extends RuntimeException {

    public DecryptionException() {
        super();
    }

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(Throwable cause) {
        super(cause);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
