package com.sjna.teamup.common.domain.exception;

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
