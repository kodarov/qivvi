package com.kodarovs.qivvi.exceptions;

import javax.persistence.EntityNotFoundException;

public class WalletNotFoundException extends EntityNotFoundException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
