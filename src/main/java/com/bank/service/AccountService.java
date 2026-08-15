package com.bank.service;

import com.bank.dto.DashboardDTO;

import java.math.BigDecimal;

public interface AccountService {
    DashboardDTO getDashboardSummary(String accountNumber);

    void deposit(
            String accountNumber,
            BigDecimal amount,
            String description
    );

    void withdraw(
            String accountNumber,
            BigDecimal amount,
            String description
    );

    void transferFunds(
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount,
            String description);

}
