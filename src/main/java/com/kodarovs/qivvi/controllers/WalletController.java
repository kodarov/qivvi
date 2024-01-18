package com.kodarovs.qivvi.controllers;

import com.kodarovs.qivvi.dto.OperationDTO;
import com.kodarovs.qivvi.dto.WalletDto;
import com.kodarovs.qivvi.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/wallet")
    public ResponseEntity<?> updateWallet(@RequestBody OperationDTO operationDTO) {
        try {
            walletService.updateWallet(operationDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> getBalance(@PathVariable long walletId) {
        try {
            BigDecimal balance = walletService.getBalance(walletId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
