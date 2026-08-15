package com.bank.security;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        // Fetch the account from MySQL using the account number as the "username"
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + accountNumber));
        log.debug("User {} loaded successfully",
                account.getAccountNumber());
        // Build a Spring Security User object
        return User.builder()
                .username(account.getAccountNumber())
                .password(account.getPassword())
                .roles(account.getRole()) // Make sure your DB row has "USER" in the role column
                .build();
    }
}