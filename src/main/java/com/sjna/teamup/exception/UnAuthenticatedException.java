package com.sjna.teamup.exception;

public class UnAuthenticatedException extends RuntimeException {

    public UnAuthenticatedException() {
        super();
    }

    public UnAuthenticatedException(String message) {
        super(message);
    }

    public UnAuthenticatedException(Throwable cause) {
        super(cause);
    }

    public UnAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
