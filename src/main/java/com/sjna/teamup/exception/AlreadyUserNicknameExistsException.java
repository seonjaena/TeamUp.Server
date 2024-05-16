package com.sjna.teamup.exception;

public class AlreadyUserNicknameExistsException extends RuntimeException {

    public AlreadyUserNicknameExistsException() {
        super();
    }

    public AlreadyUserNicknameExistsException(String message) {
        super(message);
    }

    public AlreadyUserNicknameExistsException(Throwable cause) {
        super(cause);
    }

    public AlreadyUserNicknameExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
