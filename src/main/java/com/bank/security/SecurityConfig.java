package com.bank.security;

import com.bank.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for simplicity in this demo. Enable for Prod!
                .authorizeHttpRequests(auth -> auth
                        // CRITICAL FIX: Allow Spring to forward requests internally to render JSPs
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD).permitAll()
                        // Allow public access to login page and static assets
                        .requestMatchers("/login", "/css/**", "/js/**", "/WEB-INF/jsp/**").permitAll()
                        // Require authentication for everything else
                        .requestMatchers("/app/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/app/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    // BCrypt is the enterprise standard for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AUTO-FIX: This will run once on startup, generate a real BCrypt hash, and fix the database
    @Bean
    public CommandLineRunner fixPasswordHash(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            accountRepository.findByAccountNumber("ACC100200300").ifPresent(account -> {
                // Generate the mathematically correct hash for "password123"
                account.setPassword(passwordEncoder.encode("password123"));
                accountRepository.save(account);
                System.out.println("✅ SUCCESS: Database password hash has been corrected!");
            });
        };
    }
}