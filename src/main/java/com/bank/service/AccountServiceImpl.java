package com.bank.service;

import com.bank.dto.DashboardDTO;
import com.bank.dto.TransactionDTO;
import com.bank.entity.*;
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
    public void deposit(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() ->
                new BankException("Account not found"));
        validateAccount(account);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .description(description != null && !description.isBlank()
                        ? description
                        : "Cash Deposit"
                )
                .referenceNumber(generateReference())
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        log.info("Deposit successful | Account = {} | Amount={}", accountNumber, account);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = Exception.class)
    public void withdraw(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BankException("Account not found"));

        validateAccount(account);

        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance | Account ={} | Balance = {} | Requested={}",
                    accountNumber,
                    account.getBalance(),
                    amount);
            throw new BankException("Insufficient funds.");
        }
        account.setBalance(
                account.getBalance().subtract(amount)
        );

        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .description(description != null && !description.isBlank()
                        ? description : "Cash withdrawal"
                )
                .referenceNumber(generateReference())
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        log.info(
                "Withdrawal successful | Account={} | Amount={}",
                accountNumber,
                amount
        );
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void transferFunds(String fromAccountNum, String toAccountNum, BigDecimal amount, String desc) {
        log.debug("Validating transfer parameters...");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("User {} attempted a transfer with an invalid amount: {}", fromAccountNum, amount);
            throw new BankException("Transfer amount must be greater than zero.");
        }
        if (fromAccountNum.equals(toAccountNum)) {
            log.warn("User {} attempted to transfer funds to their own account.", fromAccountNum);
            throw new BankException("Cannot transfer funds to the same account.");
        }

        //modification added
        String firstAccount;
        String secondAccount;

        if (fromAccountNum.compareTo(toAccountNum) < 0) {
            firstAccount = fromAccountNum;
            secondAccount = toAccountNum;
        } else {
            firstAccount = toAccountNum;
            secondAccount = fromAccountNum;
        }

        Account first =
                accountRepository.findByAccountNumberForUpdate(firstAccount)
                        .orElseThrow(() ->
                                new BankException(
                                        "Account not found: " + firstAccount
                                ));

        Account second =
                accountRepository.findByAccountNumberForUpdate(secondAccount)
                        .orElseThrow(() ->
                                new BankException(
                                        "Account not found: " + secondAccount
                                ));
        //end

        Account source =
                first.getAccountNumber().equals(fromAccountNum)
                        ? first
                        : second;

        Account destination =
                first.getAccountNumber().equals(toAccountNum)
                        ? first
                        : second;

        validateAccount(source);
        validateAccount(destination);

        if (source.getBalance().compareTo(amount) < 0) {

            throw new BankException(
                    "Insufficient funds."
            );
        }

        source.setBalance(
                source.getBalance().subtract(amount)
        );

        destination.setBalance(
                destination.getBalance().add(amount)
        );

        accountRepository.save(source);
        accountRepository.save(destination);

        String reference = generateReference();

        Transaction debit = Transaction.builder()
                .account(source)
                .amount(amount)
                .type(TransactionType.TRANSFER_OUT)
                .status(TransactionStatus.SUCCESS)
                .description("Transfer to " + toAccountNum+ " - " + desc
                )
                .referenceNumber(reference + "-D")
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction credit = Transaction.builder()
                .account(destination)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .status(TransactionStatus.SUCCESS)
                .description("Transfer from "+ fromAccountNum+ " - " + desc)
                .referenceNumber(reference + "-C")
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(debit);
        transactionRepository.save(credit);

        log.info(
                "Transfer successful | From={} | To={} | Amount={} | Reference={}",
                fromAccountNum,
                toAccountNum,
                amount,
                reference
        );
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BankException(
                    "Amount must be greater than zero."
            );
        }
    }


    private void validateAccount(Account account) {

        if (account.getStatus() != AccountStatus.ACTIVE) {

            throw new BankException(
                    "Account is not active. Current status: "
                            + account.getStatus()
            );
        }
    }


    private String generateReference() {

        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
