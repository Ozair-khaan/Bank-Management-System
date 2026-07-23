package com.bank.controller;

import com.bank.dto.DashboardDTO;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BankController {

    private final AccountService accountService;

    // Route for the custom login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/app/dashboard")
    public String viewDashboard(Model model, Principal principal) {
        // Dynamically fetch the logged-in user's account number
        String loggedInUser = principal.getName();
        log.info("User {} accessed the dashboard.", loggedInUser);

        DashboardDTO dashboardData = accountService.getDashboardSummary(loggedInUser);
        model.addAttribute("dashboard", dashboardData);
        return "dashboard";
    }

    @GetMapping("/app/transfer")
    public String showTransferForm(Model model, Principal principal) {
        log.debug("User {} loaded the transfer form.", principal.getName());
        model.addAttribute("fromAccount", principal.getName());
        return "transfer";
    }

    @PostMapping("/app/transfer")
    public String processTransfer(@RequestParam String toAccount,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description,
                                  Model model,
                                  Principal principal) {
        String loggedInUser = principal.getName();
        log.info("Transfer initiated: From {} to {} | Amount: {}", loggedInUser, toAccount, amount);

        try {
            accountService.transferFunds(loggedInUser, toAccount, amount, description);
            log.info("Transfer successful: From {} to {}", loggedInUser, toAccount);
            model.addAttribute("successMessage", "Transfer of $" + amount + " completed successfully.");
        } catch (Exception e) {
            log.error("Transfer failed for user {}. Reason: {}", loggedInUser, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("fromAccount", loggedInUser);
        return "transfer";
    }
}