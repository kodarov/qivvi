package com.kodarovs.qivvi.controllers;

import com.kodarovs.qivvi.dto.OperationDTO;
import com.kodarovs.qivvi.entities.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Wallets
 * wallet1 amount = 10000
 * wallet2 amount = 20000
 * wallet3 amount = 30000
 * wallet4 amount = 0
 * wallet5 amount = 100_000
 */

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WalletController walletController;
    @Autowired
    private TestRestTemplate restTemplate;
    private String updateWalletUri;
    private String balanceUri;
    private UUID walletId1, walletId2, walletId3, walletId4, walletId5;
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    public void setUp() throws Exception {
        walletId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        walletId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        walletId3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
        walletId4 = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");
        walletId5 = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");
        updateWalletUri = "http://localhost:" + port + "/api/v1/wallet";
        balanceUri = "http://localhost:" + port + "/api/v1/wallets/";
    }

    @Test
    void contextLoads() throws Exception {
        assertThat(walletController).isNotNull();
    }

    @Test
    void whenDeposit1000ThenResponse200() {
        OperationDTO operationDeposit = new OperationDTO();
        operationDeposit.setValletId(walletId1);
        operationDeposit.setTransactionType(TransactionType.DEPOSIT);
        operationDeposit.setAmount(BigDecimal.valueOf(1000));
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(operationDeposit),
                OperationDTO.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<?> balanceResponse = restTemplate.getForEntity(balanceUri + walletId1, BigDecimal.class);
        assertThat(balanceResponse.getBody()).isEqualTo(BigDecimal.valueOf(11000));
    }

    @Test
    void whenDepositMinus1000ThenResponse400() {
        OperationDTO operationDeposit = new OperationDTO();
        operationDeposit.setValletId(walletId2);
        operationDeposit.setTransactionType(TransactionType.DEPOSIT);
        operationDeposit.setAmount(BigDecimal.valueOf(-1000));
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(operationDeposit),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<?> balanceResponse = restTemplate.getForEntity(balanceUri + walletId2, BigDecimal.class);
        assertThat(balanceResponse.getBody()).isEqualTo(BigDecimal.valueOf(20000));
    }

    @Test
    void whenWalletUUIDNotFoundThenResponse404() {
        OperationDTO operationDeposit = new OperationDTO();
        UUID subWalletId = UUID.randomUUID();
        operationDeposit.setValletId(subWalletId);
        operationDeposit.setTransactionType(TransactionType.DEPOSIT);
        operationDeposit.setAmount(BigDecimal.valueOf(1000));
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(operationDeposit),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenWithdrawMoreBalanceThenResponse403() {
        OperationDTO operationDeposit = new OperationDTO();
        operationDeposit.setValletId(walletId3);
        operationDeposit.setTransactionType(TransactionType.WITHDRAW);
        operationDeposit.setAmount(BigDecimal.valueOf(1000000000));
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(operationDeposit),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void whenWithdraw10000ThenResponse200() {
        OperationDTO operationDeposit = new OperationDTO();
        operationDeposit.setValletId(walletId3);
        operationDeposit.setTransactionType(TransactionType.WITHDRAW);
        operationDeposit.setAmount(BigDecimal.valueOf(10000));
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(operationDeposit),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<?> balanceResponse = restTemplate.getForEntity(balanceUri + walletId3, BigDecimal.class);
        assertThat(balanceResponse.getBody()).isEqualTo(BigDecimal.valueOf(20000));
    }

    @Test
    void whenInvalidJsonFormatThenResponse404() {
        String invalidJson = "{invalidJson}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                updateWalletUri,
                HttpMethod.PATCH,
                new HttpEntity<>(entity),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void when1000DepositsPer10ThreadsBy10Then100000() throws Exception {
        int numberOfThreads = 10;
        int depositsPerThread = 1000;
        BigDecimal deposit = BigDecimal.valueOf(10);
        BigDecimal expect = BigDecimal.valueOf((long) numberOfThreads * depositsPerThread * deposit.intValue());
        CountDownLatch latch = new CountDownLatch(numberOfThreads * depositsPerThread);
        Runnable depositTask = () -> {
            try {
                for (int i = 0; i < depositsPerThread; i++) {
                    OperationDTO operationDeposit = new OperationDTO();
                    operationDeposit.setValletId(walletId4);
                    operationDeposit.setTransactionType(TransactionType.DEPOSIT);
                    operationDeposit.setAmount(deposit);

                    ResponseEntity<?> response = restTemplate.exchange(
                            updateWalletUri,
                            HttpMethod.PATCH,
                            new HttpEntity<>(operationDeposit),
                            OperationDTO.class
                    );
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    latch.countDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(depositTask).start();
        }
        latch.await();
        BigDecimal result = restTemplate.getForEntity(balanceUri + walletId4, BigDecimal.class).getBody();
        assertThat(result).isEqualTo(expect);
    }
    @Test
    void when1000WithdrawPer10ThreadsBy10Then0() throws Exception {
        int numberOfThreads = 10;
        int withdrawPerThread = 1000;
        BigDecimal withdraw = BigDecimal.valueOf(10);
        BigDecimal expect = BigDecimal.valueOf(0);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * withdrawPerThread);
        Runnable depositTask = () -> {
            try {
                for (int i = 0; i < withdrawPerThread; i++) {
                    OperationDTO operationDeposit = new OperationDTO();
                    operationDeposit.setValletId(walletId5);
                    operationDeposit.setTransactionType(TransactionType.WITHDRAW);
                    operationDeposit.setAmount(withdraw);

                    ResponseEntity<?> response = restTemplate.exchange(
                            updateWalletUri,
                            HttpMethod.PATCH,
                            new HttpEntity<>(operationDeposit),
                            OperationDTO.class
                    );
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    latch.countDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(depositTask).start();
        }
        latch.await();
        BigDecimal result = restTemplate.getForEntity(balanceUri + walletId5, BigDecimal.class).getBody();
        assertThat(result).isEqualTo(expect);
    }
}