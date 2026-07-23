package com.bank.service;

import com.bank.dto.DashboardDTO;
import com.bank.dto.TransactionDTO;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.exception.BankException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO getDashboardSummary(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankException("Account not found"));

        List<Transaction> transactions = transactionRepository.findTop10ByAccountIdOrderByTransactionDateDesc(account.getId());
        List<TransactionDTO> transactionDTOs = transactions.stream().map(txn ->
                TransactionDTO.builder()
                        .date(txn.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .type(txn.getType().name())
                        .amount(txn.getAmount())
                        .description(txn.getDescription())
                        .referenceNumber(txn.getReferenceNumber())
                        .build()
        ).collect(Collectors.toList());

        return DashboardDTO.builder()
                .accountHolderName(account.getAccountHolderName())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .availableBalance(account.getBalance())
                .recentTransactions(transactionDTOs)
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void transferFunds(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String description) {
        log.debug("Validating transfer parameters...");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("User {} attempted a transfer with an invalid amount: {}", fromAccountNumber, amount);
            throw new BankException("Transfer amount must be greater than zero");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            log.warn("User {} attempted to transfer funds to their own account.", fromAccountNumber);
            throw new BankException("Cannot transfer funds to the same account.");
        }

        Account source = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new BankException("Source account not found"));

        Account destination = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new BankException("Destination account not found"));

        if (source.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds. User {} attempted to transfer {} but has only {}", fromAccountNumber, amount, source.getBalance());
            throw new BankException("Insufficient funds");
        }

        log.debug("Deducting {} from {} and adding to {}", amount, fromAccountNumber, toAccountNumber);
        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        // Deduct and Add
        accountRepository.save(source);
        accountRepository.save(destination);

        String refNum = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Record Transactions
        Transaction debit = Transaction.builder()
                .account(source)
                .amount(amount)
                .type(TransactionType.TRANSFER_OUT)
                .description("Transfer to " + toAccountNumber + " - " + description)
                .referenceNumber("TXN-" + refNum + "-D")
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction credit = Transaction.builder()
                .account(destination)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .description("Transfer from " + fromAccountNumber + " - " + description)
                .referenceNumber("TXN-" + refNum + "-C")
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(debit);
        transactionRepository.save(credit);
    }
}
