package com.kodarovs.qivvi.entities;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "operations")
@Data
public class Operation {
    public Operation(){
        this.operationDate = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private TransactionType operation;
    private LocalDateTime operationDate;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "wallets_id", nullable = false)
    private Wallet wallet;
}
