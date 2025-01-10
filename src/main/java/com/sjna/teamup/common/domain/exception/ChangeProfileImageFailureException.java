package com.sjna.teamup.common.domain.exception;

public class ChangeProfileImageFailureException extends RuntimeException {

    public ChangeProfileImageFailureException() {
        super();
    }

    public ChangeProfileImageFailureException(String message) {
        super(message);
    }

    public ChangeProfileImageFailureException(Throwable cause) {
        super(cause);
    }

    public ChangeProfileImageFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
