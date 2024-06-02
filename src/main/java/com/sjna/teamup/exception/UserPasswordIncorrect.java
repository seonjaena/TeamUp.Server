package com.sjna.teamup.exception;

public class UserPasswordIncorrect extends RuntimeException {

    public UserPasswordIncorrect() {
        super();
    }

    public UserPasswordIncorrect(String message) {
        super(message);
    }

    public UserPasswordIncorrect(Throwable cause) {
        super(cause);
    }

    public UserPasswordIncorrect(String message, Throwable cause) {
        super(message, cause);
    }

}
