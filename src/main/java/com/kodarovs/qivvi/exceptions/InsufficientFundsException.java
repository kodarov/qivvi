package com.kodarovs.qivvi.exceptions;

import javax.persistence.EntityNotFoundException;

public class InsufficientFundsException extends EntityNotFoundException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
