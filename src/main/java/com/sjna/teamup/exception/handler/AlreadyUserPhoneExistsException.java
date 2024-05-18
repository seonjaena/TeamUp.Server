package com.sjna.teamup.exception.handler;

public class AlreadyUserPhoneExistsException extends RuntimeException {

    public AlreadyUserPhoneExistsException() {
        super();
    }

    public AlreadyUserPhoneExistsException(String message) {
        super(message);
    }

    public AlreadyUserPhoneExistsException(Throwable cause) {
        super(cause);
    }

    public AlreadyUserPhoneExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
