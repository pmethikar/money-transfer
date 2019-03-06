package com.revolut.challenge.exception;

public class MoneyTransferException extends RuntimeException {

    public MoneyTransferException(String message) {
        super(message);
    }
}
