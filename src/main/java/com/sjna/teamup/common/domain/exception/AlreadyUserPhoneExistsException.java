package com.sjna.teamup.common.domain.exception;

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
