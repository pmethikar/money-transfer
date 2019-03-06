package com.revolut.challenge.exception;

public class InvalidAccountException extends MoneyTransferException {

    public InvalidAccountException(String message) {
        super(message);
    }
}