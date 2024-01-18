package com.kodarovs.qivvi.entities;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "operations")
@Data
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class Operation {
    public Operation(){
        this.operationDate = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "transaction_type")
    @Type(type = "pgsql_enum")
    private TransactionType operation;
    private LocalDateTime operationDate;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "wallets_id", nullable = false)
    private Wallet wallet;
}
