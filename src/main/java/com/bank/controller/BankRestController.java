package com.bank.controller;

import com.bank.dto.DashboardDTO;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequestMapping("/api/v1/bank")
@RequiredArgsConstructor
public class BankRestController {

    private final AccountService accountService;

    @GetMapping("/dashboard")
    // Use @Parameter(hidden = true) so Swagger doesn't ask the user to type in a "Principal"
    public ResponseEntity<DashboardDTO> getDashboard(@Parameter(hidden = true) Principal principal) {
        String loggedInUser = principal.getName();
        log.info("API: Fetching dashboard for user {}", loggedInUser);

        DashboardDTO dashboardData = accountService.getDashboardSummary(loggedInUser);
        return ResponseEntity.ok(dashboardData);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> processTransfer(@RequestParam String toAccount,
                                                  @RequestParam BigDecimal amount,
                                                  @RequestParam String description,
                                                  @Parameter(hidden = true) Principal principal) {
        String loggedInUser = principal.getName();
        log.info("API: Transfer initiated: From {} to {} | Amount: {}", loggedInUser, toAccount, amount);

        try {
            accountService.transferFunds(loggedInUser, toAccount, amount, description);
            return ResponseEntity.ok("Transfer of $" + amount + " completed successfully.");
        } catch (Exception e) {
            log.error("API: Transfer failed. Reason: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}