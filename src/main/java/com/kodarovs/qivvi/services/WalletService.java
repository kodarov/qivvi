package com.kodarovs.qivvi.services;

import com.kodarovs.qivvi.dto.OperationDTO;
import com.kodarovs.qivvi.entities.Operation;
import com.kodarovs.qivvi.entities.TransactionType;
import com.kodarovs.qivvi.entities.Wallet;
import com.kodarovs.qivvi.repositories.OperationRepository;
import com.kodarovs.qivvi.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {
    private final OperationRepository operationRepository;
    private final WalletRepository walletRepository;

    public BigDecimal getBalance(long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return wallet.getBalance();
    }

    @Transactional
    public void updateWallet(OperationDTO operationDto) {
        Wallet wallet = walletRepository.findById(operationDto.getValletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        Operation operation = new Operation();
        operation.setWallet(wallet);
        switch (operationDto.getTransactionType()) {
            case DEPOSIT -> {
                operation.setOperation(TransactionType.DEPOSIT);
                operation.setAmount(operationDto.getAmount());
                wallet.setBalance(wallet.getBalance().add(operationDto.getAmount()));
            }
            case WITHDRAW -> {
                BigDecimal balance = wallet.getBalance().subtract(operationDto.getAmount());
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Insufficient funds!");
                }
                operation.setOperation(TransactionType.WITHDRAW);
                operation.setAmount(operationDto.getAmount());
                wallet.setBalance(balance);
            }
            default -> throw new RuntimeException("Unsupported transaction type!");
        }
        operationRepository.save(operation);
        walletRepository.save(wallet);
    }
}

