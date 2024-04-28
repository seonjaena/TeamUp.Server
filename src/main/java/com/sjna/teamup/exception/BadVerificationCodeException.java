package com.sjna.teamup.exception;

public class BadVerificationCodeException extends RuntimeException {

    public BadVerificationCodeException() {
        super();
    }

    public BadVerificationCodeException(String message) {
        super(message);
    }

    public BadVerificationCodeException(Throwable cause) {
        super(cause);
    }

    public BadVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
