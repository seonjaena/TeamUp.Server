package com.sjna.teamup.exception;

public class JwtExpirationException extends RuntimeException {

    public JwtExpirationException() {
        super();
    }

    public JwtExpirationException(String message) {
        super(message);
    }

    public JwtExpirationException(Throwable cause) {
        super(cause);
    }

    public JwtExpirationException(String message, Throwable cause) {
        super(message, cause);
    }

}
