package com.kodarovs.qivvi.repositories;

import com.kodarovs.qivvi.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findById(UUID id);
}
