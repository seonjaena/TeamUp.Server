package com.sjna.teamup.exception;

public class UserYoungException extends RuntimeException {

    public UserYoungException() {
        super();
    }

    public UserYoungException(String message) {
        super(message);
    }

    public UserYoungException(Throwable cause) {
        super(cause);
    }

    public UserYoungException(String message, Throwable cause) {
        super(message, cause);
    }

}
