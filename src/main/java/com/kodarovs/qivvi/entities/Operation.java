package com.kodarovs.qivvi.entities;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "operations")
@Data
@NoArgsConstructor
@TypeDef(name = "transaction_type",
        typeClass = PostgreSQLEnumType.class)
public class Operation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "operation", columnDefinition = "transaction_type")
    @Type(type = "transaction_type")
    private TransactionType operation;
    
    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallets_id", nullable = false)
    private Wallet wallet;
    
    @Column(name = "balance_before", precision = 19, scale = 2)
    private BigDecimal balanceBefore;
    
    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(name = "operation_id", unique = true)
    private String operationId;
    
    public Operation(TransactionType operation, BigDecimal amount, Wallet wallet, 
                    BigDecimal balanceBefore, BigDecimal balanceAfter) {
        this.operation = operation;
        this.amount = amount;
        this.wallet = wallet;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.operationDate = LocalDateTime.now();
        this.operationId = UUID.randomUUID().toString();
    }
    
    @PrePersist
    protected void onCreate() {
        if (operationDate == null) {
            operationDate = LocalDateTime.now();
        }
        if (operationId == null) {
            operationId = UUID.randomUUID().toString();
        }
    }
}
