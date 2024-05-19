package com.sjna.teamup.exception;

public class SendSMSFailureException extends RuntimeException {

    public SendSMSFailureException() {
        super();
    }

    public SendSMSFailureException(String message) {
        super(message);
    }

    public SendSMSFailureException(Throwable cause) {
        super(cause);
    }

    public SendSMSFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
