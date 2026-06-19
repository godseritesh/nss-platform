package com.nssplatform.auth.controller;

import com.nssplatform.auth.dto.AuthResponse;
import com.nssplatform.auth.dto.LoginRequest;
import com.nssplatform.auth.dto.PasswordResetConfirm;
import com.nssplatform.auth.dto.PasswordResetRequest;
import com.nssplatform.auth.dto.RegisterRequest;
import com.nssplatform.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty() || request.getUsername().length() > 50) {
                throw new RuntimeException("Invalid username");
            }
            if (request.getEmail() == null || request.getEmail().isEmpty() || !request.getEmail().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
                throw new RuntimeException("Invalid email");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
                throw new RuntimeException("Invalid password");
            }
            if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty() || !request.getConfirmPassword().equals(request.getPassword())) {
                throw new RuntimeException("Passwords do not match");
            }
            return ResponseEntity.status(201).body(authService.register(request, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty() || request.getUsername().length() > 50) {
                throw new RuntimeException("Invalid username");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
                throw new RuntimeException("Invalid password");
            }
            return ResponseEntity.ok(authService.login(request, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        try {
            authService.logout(response);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal String email) {
        try {
            if (email == null || email.isEmpty() || !email.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
                throw new RuntimeException("Invalid email");
            }
            return ResponseEntity.ok(authService.getCurrentUser(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty() || !request.getEmail().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
                throw new RuntimeException("Invalid email");
            }
            authService.requestPasswordReset(request);
            return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link has been sent."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetConfirm request) {
        try {
            if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
                throw new RuntimeException("Invalid password");
            }
            if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty() || !request.getConfirmPassword().equals(request.getPassword())) {
                throw new RuntimeException("Passwords do not match");
            }
            authService.confirmPasswordReset(request);
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}