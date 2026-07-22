package com.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionDTO {
    private String date;
    private String type;
    private BigDecimal amount;
    private String description;
    private String referenceNumber;
}

