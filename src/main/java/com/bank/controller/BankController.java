package com.bank.controller;

import com.bank.dto.DashboardDTO;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class BankController {
    private final AccountService accountService;
    private final String LOGGED_IN_USER_ACCOUNT = "ACC100200300";

    @GetMapping("/dashboard")
    public String viewDashBoard(Model model){
        DashboardDTO dashboardData = accountService.getDashboardSummary(LOGGED_IN_USER_ACCOUNT);
        model.addAttribute("dashboard", dashboardData);
        return "dashboard";
    }

    @GetMapping("/transfer")
    public String showTransferFrom(Model model){
        model.addAttribute("fromAccount", LOGGED_IN_USER_ACCOUNT);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam String toAccount,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description,
                                  Model model){
        try {
            accountService.transferFunds(LOGGED_IN_USER_ACCOUNT, toAccount, amount, description);
            model.addAttribute("successMessage", "Transfer from $"+amount+" completed successfully");
        }
        catch (Exception e){
            model.addAttribute("errorMessage",e.getMessage());
        }
        model.addAttribute("fromAccount", LOGGED_IN_USER_ACCOUNT);
        return "transfer";
    }
}
