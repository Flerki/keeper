package com.amairovi.keeper.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String msg) {
        super(msg);
    }
}
