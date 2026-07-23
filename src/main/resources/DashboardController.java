package com.bank.controller;

import com.bank.dto.DashboardDTO;
import com.bank.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final AccountService accountService;


    public DashboardController(AccountService accountService) {
        this.accountService = accountService;
    }

    public String showDashboard(Model model) {
        String username = "current_user";

        DashboardDTO dashboardData = accountService.getDashboardSummary(username);
        model.addAttribute("dashboard", dashboardData);

        // Resolves to /WEB-INF/jsp/dashboard.jsp
        return "dashboard";
    }
}
