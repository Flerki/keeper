package com.amairovi.keeper.exception;

public class ItemDoesNotExistException extends RuntimeException{
    public ItemDoesNotExistException(String msg) {
        super(msg);
    }
}
