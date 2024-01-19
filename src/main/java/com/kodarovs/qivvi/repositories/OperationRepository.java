package com.kodarovs.qivvi.repositories;

import com.kodarovs.qivvi.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation,Long> {
}
