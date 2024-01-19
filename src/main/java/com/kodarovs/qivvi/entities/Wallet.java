package com.kodarovs.qivvi.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity(name = "wallets")
@Data
public class Wallet {
    public Wallet(){
        this.id = UUID.randomUUID();
    }
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private BigDecimal balance;
    @OneToMany(mappedBy = "wallet")
    private List<Operation> operations;
}
