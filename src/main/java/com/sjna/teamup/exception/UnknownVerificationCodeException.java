package com.sjna.teamup.exception;

public class UnknownVerificationCodeException extends RuntimeException {

    public UnknownVerificationCodeException() {
        super();
    }

    public UnknownVerificationCodeException(String message) {
        super(message);
    }

    public UnknownVerificationCodeException(Throwable cause) {
        super(cause);
    }

    public UnknownVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
