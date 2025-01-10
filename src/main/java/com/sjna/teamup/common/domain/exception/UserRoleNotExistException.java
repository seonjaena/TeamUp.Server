package com.sjna.teamup.common.domain.exception;

public class UserRoleNotExistException extends RuntimeException {

    public UserRoleNotExistException() {
        super();
    }

    public UserRoleNotExistException(String message) {
        super(message);
    }

    public UserRoleNotExistException(Throwable cause) {
        super(cause);
    }

    public UserRoleNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
