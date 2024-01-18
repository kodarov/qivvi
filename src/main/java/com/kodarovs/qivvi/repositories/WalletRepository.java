package com.kodarovs.qivvi.repositories;

import com.kodarovs.qivvi.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from wallets w where w.id = ?1")
    Optional<Wallet> findByIdForUpdate(Long id);
}
