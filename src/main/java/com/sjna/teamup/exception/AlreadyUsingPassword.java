package com.sjna.teamup.exception;

public class AlreadyUsingPassword extends RuntimeException {

    public AlreadyUsingPassword() {
        super();
    }

    public AlreadyUsingPassword(String message) {
        super(message);
    }

    public AlreadyUsingPassword(Throwable cause) {
        super(cause);
    }

    public AlreadyUsingPassword(String message, Throwable cause) {
        super(message, cause);
    }

}
