package com.amairovi.keeper.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String userId) {
        super("User with id " + userId + " doesn't exist.");
    }
}
