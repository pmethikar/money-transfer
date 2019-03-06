package com.revolut.challenge.exception;

public class InvalidAmountException extends MoneyTransferException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
