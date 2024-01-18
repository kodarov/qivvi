package com.kodarovs.qivvi.dto;

import com.kodarovs.qivvi.entities.TransactionType;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class OperationDTO {
    @NotNull(message = "Please provide a wallet ID")
    private long valletId;
    @NotNull(message = "Please provide a transaction type")
    private TransactionType transactionType;
    @NotNull(message = "Please provide an amount")
    @DecimalMin(value = "0.0", inclusive = false, message = "The amount must be positive")
    private BigDecimal amount;
}
