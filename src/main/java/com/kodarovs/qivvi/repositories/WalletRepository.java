package com.kodarovs.qivvi.repositories;

import com.kodarovs.qivvi.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
