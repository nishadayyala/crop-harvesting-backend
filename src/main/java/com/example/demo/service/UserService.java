package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapAdminEmail;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.auth.admin-email:}") String bootstrapAdminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminEmail = normalizeEmail(bootstrapAdminEmail);
    }

    @Transactional
    public User registerFarmer(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("FARMER");
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Transactional
    public void bootstrapAdmin(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail).orElseGet(() -> {
            User newAdmin = new User();
            newAdmin.setName("System administrator");
            newAdmin.setEmail(normalizedEmail);
            newAdmin.setPassword(passwordEncoder.encode(rawPassword));
            newAdmin.setRole("FARMER");
            return userRepository.save(newAdmin);
        });

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalStateException(
                    "The configured bootstrap admin email already belongs to an account with a different password.");
        }
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    public String roleFor(User user) {
        return !bootstrapAdminEmail.isBlank()
                && user.getEmail().equalsIgnoreCase(bootstrapAdminEmail)
                && "ADMIN".equalsIgnoreCase(user.getRole())
                ? "ADMIN"
                : "FARMER";
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User account = findByEmail(email);
        return org.springframework.security.core.userdetails.User
                .withUsername(account.getEmail())
                .password(account.getPassword())
                .roles(roleFor(account))
                .build();
    }

    public AuthResponse toAuthResponse(String email) {
        User user = findByEmail(email);
        return AuthResponse.from(user, roleFor(user));
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
