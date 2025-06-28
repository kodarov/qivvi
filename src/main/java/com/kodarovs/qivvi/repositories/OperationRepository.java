package com.kodarovs.qivvi.repositories;

import com.kodarovs.qivvi.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation,Long> {
    
    /**
     * Находит все операции для указанного кошелька, отсортированные по дате операции (новые сначала)
     * @param walletId ID кошелька
     * @return список операций
     */
    List<Operation> findByWalletIdOrderByOperationDateDesc(UUID walletId);
}
