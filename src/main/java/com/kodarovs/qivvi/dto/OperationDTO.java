package com.kodarovs.qivvi.dto;

import com.kodarovs.qivvi.entities.TransactionType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class OperationDTO {
    @NotBlank
    private long valletId;
    @NotBlank
    private TransactionType transactionType;
    @NotBlank
    @Size
    private BigDecimal amount;
}
