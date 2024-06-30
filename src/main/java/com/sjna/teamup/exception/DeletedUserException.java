package com.sjna.teamup.exception;

public class DeletedUserException extends RuntimeException {

    public DeletedUserException() {
        super();
    }

    public DeletedUserException(String message) {
        super(message);
    }

    public DeletedUserException(Throwable cause) {
        super(cause);
    }

    public DeletedUserException(String message, Throwable cause) {
        super(message, cause);
    }

}
