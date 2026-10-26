package com.bank.controller;

import com.bank.dto.DashboardDTO;
import com.bank.dto.DepositRequest;
import com.bank.dto.TransferRequest;
import com.bank.dto.WithdrawRequest;
import com.bank.service.AccountService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/bank")
@RequiredArgsConstructor
public class BankRestController {

    private final AccountService accountService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(@Parameter(hidden = true) Principal principal) {
        String loggedInUser = principal.getName();
        log.info("API: Fetching dashboard for user {}", loggedInUser);
        return ResponseEntity.ok(accountService.getDashboardSummary(loggedInUser));
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@Valid @RequestBody DepositRequest request,
                                          @Parameter(hidden = true) Principal principal) {
        accountService.deposit(
                principal.getName(),
                request.getAmount(),
                request.getDescription()
        );

        return ResponseEntity.ok("Deposit successful. Amount: " + request.getAmount());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WithdrawRequest request,
                                           @Parameter(hidden = true) Principal principal) {

        accountService.withdraw(principal.getName(), request.getAmount(), request.getDescription());

        return ResponseEntity.ok("Withdrawal successful. Amount: " + request.getAmount());
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request, @Parameter(hidden = true) Principal principal) {
        accountService.transferFunds(
                principal.getName(),
                request.getToAccount(),
                request.getAmount(),
                request.getDescription()
        );

        return ResponseEntity.ok("Transfer successful. Amount: " + request.getAmount());
    }
}