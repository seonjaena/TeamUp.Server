package com.sjna.teamup.exception;

public class AlreadyUserEmailExistsException extends RuntimeException {

    public AlreadyUserEmailExistsException() {
        super();
    }

    public AlreadyUserEmailExistsException(String message) {
        super(message);
    }

    public AlreadyUserEmailExistsException(Throwable cause) {
        super(cause);
    }

    public AlreadyUserEmailExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
