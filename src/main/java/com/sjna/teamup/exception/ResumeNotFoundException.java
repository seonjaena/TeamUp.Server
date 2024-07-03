package com.sjna.teamup.exception;

public class ResumeNotFoundException extends RuntimeException {

    public ResumeNotFoundException() {
        super();
    }

    public ResumeNotFoundException(String message) {
        super(message);
    }

    public ResumeNotFoundException(Throwable cause) {
        super(cause);
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
