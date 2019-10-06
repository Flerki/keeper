package com.amairovi.keeper.exception;

public class PlaceDoesNotExistException extends RuntimeException {
    public PlaceDoesNotExistException(String msg) {
        super(msg);
    }
}
