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
        if (request.getUsername() == null || request.getUsername().isEmpty() || request.getUsername().length() > 50) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getEmail() == null || request.getEmail().isEmpty() || !request.getEmail().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty() || !request.getConfirmPassword().equals(request.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(authService.register(request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        if (request.getUsername() == null || request.getUsername().isEmpty() || request.getUsername().length() > 50) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal String email) {
        if (email == null || email.isEmpty() || !email.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty() || !request.getEmail().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")) {
            return ResponseEntity.badRequest().build();
        }
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetConfirm request) {
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() < 8) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty() || !request.getConfirmPassword().equals(request.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
    }
}