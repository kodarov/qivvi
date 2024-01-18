package com.kodarovs.qivvi.dto;

import com.kodarovs.qivvi.entities.TransactionType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class WalletDto {
    @NotBlank
    private Long walletId;
    @NotBlank
    private TransactionType transactionType;
    @Size
    private BigDecimal amount;
}
