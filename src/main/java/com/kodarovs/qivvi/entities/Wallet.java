package com.kodarovs.qivvi.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "wallets")
@Data
public class Wallet {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private BigDecimal balance;
    @OneToMany(mappedBy = "wallet")
    private List<Operation> operations;
}
