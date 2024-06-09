package com.sjna.teamup.exception;

public class OriginPasswordIncorrect extends RuntimeException {

    public OriginPasswordIncorrect() {
        super();
    }

    public OriginPasswordIncorrect(String message) {
        super(message);
    }

    public OriginPasswordIncorrect(Throwable cause) {
        super(cause);
    }

    public OriginPasswordIncorrect(String message, Throwable cause) {
        super(message, cause);
    }

}
