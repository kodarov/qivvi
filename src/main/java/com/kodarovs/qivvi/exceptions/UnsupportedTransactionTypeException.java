package com.kodarovs.qivvi.exceptions;

public class UnsupportedTransactionTypeException extends RuntimeException{
    public UnsupportedTransactionTypeException(String message) {
        super(message);
    }
}
