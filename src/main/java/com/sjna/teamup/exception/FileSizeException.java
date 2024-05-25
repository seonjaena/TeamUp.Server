package com.sjna.teamup.exception;

public class FileSizeException extends RuntimeException {

    public FileSizeException() {
        super();
    }

    public FileSizeException(String message) {
        super(message);
    }

    public FileSizeException(Throwable cause) {
        super(cause);
    }

    public FileSizeException(String message, Throwable cause) {
        super(message, cause);
    }

}
