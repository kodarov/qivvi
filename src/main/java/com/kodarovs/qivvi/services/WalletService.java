package com.kodarovs.qivvi.services;

import com.kodarovs.qivvi.dto.OperationDTO;
import com.kodarovs.qivvi.entities.Operation;
import com.kodarovs.qivvi.entities.TransactionType;
import com.kodarovs.qivvi.entities.Wallet;
import com.kodarovs.qivvi.exceptions.InsufficientFundsException;
import com.kodarovs.qivvi.exceptions.UnsupportedTransactionTypeException;
import com.kodarovs.qivvi.exceptions.WalletNotFoundException;
import com.kodarovs.qivvi.repositories.OperationRepository;
import com.kodarovs.qivvi.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {
    private final OperationRepository operationRepository;
    private final WalletRepository walletRepository;
    
    // Константы для сообщений об ошибках
    private static final String WALLET_NOT_FOUND_MESSAGE = "Wallet not found with ID: %s";
    private static final String INSUFFICIENT_FUNDS_MESSAGE = "Insufficient funds! Current balance: %s, requested amount: %s";
    private static final String UNSUPPORTED_TRANSACTION_MESSAGE = "Unsupported transaction type: %s";
    private static final String NULL_WALLET_ID_MESSAGE = "Wallet ID cannot be null";
    private static final String NULL_OPERATION_DTO_MESSAGE = "Operation DTO cannot be null";
    private static final String NULL_TRANSACTION_TYPE_MESSAGE = "Transaction type cannot be null";
    private static final String NULL_AMOUNT_MESSAGE = "Amount cannot be null";
    private static final String NEGATIVE_AMOUNT_MESSAGE = "Amount must be positive";

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal getBalance(UUID walletId) {
        Assert.notNull(walletId, NULL_WALLET_ID_MESSAGE);
        
        log.debug("Getting balance for wallet: {}", walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(String.format(WALLET_NOT_FOUND_MESSAGE, walletId)));
        
        log.info("Retrieved balance {} for wallet: {}", wallet.getBalance(), walletId);
        return wallet.getBalance();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateWallet(OperationDTO operationDto) {
        validateOperation(operationDto);
        
        UUID walletId = operationDto.getWalletId();
        log.info("Processing {} operation for wallet: {} with amount: {}", 
                operationDto.getTransactionType(), walletId, operationDto.getAmount());
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(String.format(WALLET_NOT_FOUND_MESSAGE, walletId)));
        
        BigDecimal oldBalance = wallet.getBalance();
        BigDecimal newBalance = processTransaction(wallet, operationDto);
        
        // Создаем и сохраняем операцию
        Operation operation = new Operation(
            operationDto.getTransactionType(),
            operationDto.getAmount(),
            wallet,
            oldBalance,
            newBalance
        );
        operationRepository.save(operation);
        
        // Обновляем баланс кошелька
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        log.info("Successfully processed {} operation for wallet: {}. Balance changed from {} to {}", 
                operationDto.getTransactionType(), walletId, oldBalance, newBalance);
    }
    
    private void validateOperation(OperationDTO operationDto) {
        Assert.notNull(operationDto, NULL_OPERATION_DTO_MESSAGE);
        Assert.notNull(operationDto.getWalletId(), NULL_WALLET_ID_MESSAGE);
        Assert.notNull(operationDto.getTransactionType(), NULL_TRANSACTION_TYPE_MESSAGE);
        Assert.notNull(operationDto.getAmount(), NULL_AMOUNT_MESSAGE);
        Assert.isTrue(operationDto.getAmount().compareTo(BigDecimal.ZERO) > 0, NEGATIVE_AMOUNT_MESSAGE);
    }
    
    private BigDecimal processTransaction(Wallet wallet, OperationDTO operationDto) {
        return switch (operationDto.getTransactionType()) {
            case DEPOSIT -> {
                BigDecimal newBalance = wallet.getBalance().add(operationDto.getAmount());
                log.debug("Processing deposit: {} + {} = {}", wallet.getBalance(), operationDto.getAmount(), newBalance);
                yield newBalance;
            }
            case WITHDRAW -> {
                BigDecimal newBalance = wallet.getBalance().subtract(operationDto.getAmount());
                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                    log.warn("Insufficient funds for withdrawal. Wallet: {}, Current balance: {}, Requested amount: {}", 
                            wallet.getId(), wallet.getBalance(), operationDto.getAmount());
                    throw new InsufficientFundsException(String.format(INSUFFICIENT_FUNDS_MESSAGE, 
                            wallet.getBalance(), operationDto.getAmount()));
                }
                log.debug("Processing withdrawal: {} - {} = {}", wallet.getBalance(), operationDto.getAmount(), newBalance);
                yield newBalance;
            }
            default -> {
                log.error("Unsupported transaction type: {}", operationDto.getTransactionType());
                throw new UnsupportedTransactionTypeException(String.format(UNSUPPORTED_TRANSACTION_MESSAGE, 
                        operationDto.getTransactionType()));
            }
        };
    }
    
    /**
     * Получает историю операций для указанного кошелька
     * @param walletId ID кошелька
     * @return список операций
     */
    @Transactional(readOnly = true)
    public List<Operation> getOperationHistory(UUID walletId) {
        Assert.notNull(walletId, NULL_WALLET_ID_MESSAGE);
        
        log.debug("Getting operation history for wallet: {}", walletId);
        
        // Проверяем существование кошелька
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(String.format(WALLET_NOT_FOUND_MESSAGE, walletId));
        }
        
        List<Operation> operations = operationRepository.findByWalletIdOrderByOperationDateDesc(walletId);
        log.info("Retrieved {} operations for wallet: {}", operations.size(), walletId);
        
        return operations;
    }
}

