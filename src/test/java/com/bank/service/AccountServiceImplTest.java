package com.bank.service;

import com.bank.entity.Account;
import com.bank.exception.BankException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

// REQUIRED IMPORTS FOR JUNIT 5 AND MOCKITO
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    // InjectMocks automatically injects the mocked repositories into the service
    @InjectMocks
    private AccountServiceImpl accountService;

    private Account sourceAccount;
    private Account destAccount;

    @BeforeEach
    void setUp() {
        // Run this before every test to set up our mock data
        sourceAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC100")
                .balance(new BigDecimal("1000.00"))
                .build();

        destAccount = Account.builder()
                .id(2L)
                .accountNumber("ACC200")
                .balance(new BigDecimal("500.00"))
                .build();
    }

    @Test
    void transferFunds_SuccessfulTransfer() {
        // GIVEN (Arrange)
        when(accountRepository.findByAccountNumber("ACC100")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC200")).thenReturn(Optional.of(destAccount));

        // WHEN (Act)
        accountService.transferFunds("ACC100", "ACC200", new BigDecimal("200.00"), "Payment");

        // THEN (Assert)
        assertEquals(new BigDecimal("800.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), destAccount.getBalance());

        // Verify that the repository save methods were actually called
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void transferFunds_InsufficientFunds_ThrowsException() {
        // GIVEN
        when(accountRepository.findByAccountNumber("ACC100")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC200")).thenReturn(Optional.of(destAccount));

        // WHEN & THEN (We expect a BankException to be thrown)
        BankException exception = assertThrows(BankException.class, () -> {
            accountService.transferFunds("ACC100", "ACC200", new BigDecimal("5000.00"), "Too large");
        });

        assertEquals("Insufficient funds.", exception.getMessage());

        // Verify that we NEVER saved anything to the database because it failed
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferFunds_NegativeAmount_ThrowsException() {
        // WHEN & THEN
        BankException exception = assertThrows(BankException.class, () -> {
            accountService.transferFunds("ACC100", "ACC200", new BigDecimal("-50.00"), "Negative");
        });

        assertEquals("Transfer amount must be greater than zero.", exception.getMessage());
    }
}


