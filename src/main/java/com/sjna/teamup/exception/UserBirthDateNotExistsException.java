package com.sjna.teamup.exception;

public class UserBirthDateNotExistsException extends RuntimeException {

    public UserBirthDateNotExistsException() {
        super();
    }

    public UserBirthDateNotExistsException(String message) {
        super(message);
    }

    public UserBirthDateNotExistsException(Throwable cause) {
        super(cause);
    }

    public UserBirthDateNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
