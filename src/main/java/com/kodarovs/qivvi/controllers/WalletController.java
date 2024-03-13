package com.kodarovs.qivvi.controllers;

import com.kodarovs.qivvi.dto.OperationDTO;
import com.kodarovs.qivvi.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PatchMapping("/wallet")
    public ResponseEntity<?> updateWallet(@Valid @RequestBody OperationDTO operationDTO) {
            walletService.updateWallet(operationDTO);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> getBalance(@PathVariable UUID walletId) {
            BigDecimal balance = walletService.getBalance(walletId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}
