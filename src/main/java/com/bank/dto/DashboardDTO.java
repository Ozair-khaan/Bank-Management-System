package com.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private String accountHolderName;
    private String accountNumber;
    private String accountType;
    private BigDecimal availableBalance;
    private List<TransactionDTO> recentTransactions;
}
