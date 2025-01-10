package com.sjna.teamup.common.domain.exception;

public class SendEmailFailureException extends RuntimeException {

    public SendEmailFailureException() {
        super();
    }

    public SendEmailFailureException(String message) {
        super(message);
    }

    public SendEmailFailureException(Throwable cause) {
        super(cause);
    }

    public SendEmailFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
