package com.nssplatform.auth.service;

import com.nssplatform.auth.dto.AuthResponse;
import com.nssplatform.auth.dto.LoginRequest;
import com.nssplatform.auth.dto.PasswordResetConfirm;
import com.nssplatform.auth.dto.PasswordResetRequest;
import com.nssplatform.auth.dto.RegisterRequest;
import com.nssplatform.auth.dto.UpdateProfileRequest;
import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.auth.security.JwtTokenProvider;
import com.nssplatform.shared.exception.ConflictException;
import com.nssplatform.shared.exception.ForbiddenException;
import com.nssplatform.shared.exception.ResourceNotFoundException;
import com.nssplatform.shared.exception.UnauthorizedException;
import com.nssplatform.shared.util.EmailService;
import com.nssplatform.shared.util.InputSanitizer;
import com.nssplatform.shared.util.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RateLimiter rateLimiter;
    private final EmailService emailService;

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean secureCookie;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
            .name(InputSanitizer.sanitize(request.getName(), 100))
            .email(email)
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(User.Role.ROLE_USER)
            .verified(false)
            .build();
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        setAuthCookie(response, token);
        log.info("New user registered: id={}", user.getId());

        return buildResponse(user, "Registration successful");
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail().toLowerCase().trim();

        String rateKey = "login:" + email;
        if (!rateLimiter.isAllowed(rateKey, 10)) {
            log.warn("Rate limited login attempt for email={}", email);
            throw new UnauthorizedException("Too many login attempts. Please try again later.");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.warn("Failed login attempt for email={}: user not found", email);
                return new UnauthorizedException("Invalid email or password");
            });

        if (user.getLockoutTime() != null && user.getLockoutTime().isAfter(LocalDateTime.now())) {
            log.warn("Locked account login attempt for email={}", email);
            throw new ForbiddenException("Account is temporarily locked. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
                user.setLockoutTime(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
                log.warn("Account locked for email={} due to {} failed attempts", email, MAX_ATTEMPTS);
            }
            userRepository.save(user);
            throw new UnauthorizedException("Invalid email or password");
        }

        user.setFailedAttempts(0);
        user.setLockoutTime(null);
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        setAuthCookie(response, token);
        log.info("User logged in: id={}", user.getId());

        return buildResponse(user, "Login successful");
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("nss_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return buildResponse(user, null);
    }

    @Transactional
    public AuthResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (request.getName() != null) {
            user.setName(InputSanitizer.sanitize(request.getName(), 100));
        }

        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null
                || !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new UnauthorizedException("Current password is incorrect");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        log.info("Profile updated for user: id={}", user.getId());
        return buildResponse(user, "Profile updated");
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;

        String token = HexFormat.of().formatHex(new SecureRandom().generateSeed(32));
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String link = "https://nss-platform.onrender.com/reset-password?token=" + token;
        emailService.sendEmail(email, "Reset your NSS VIIT password",
            "<p>Click <a href=\"" + link + "\">here</a> to reset your password. This link expires in 1 hour.</p>");
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirm request) {
        User user = userRepository.findByResetToken(request.getToken())
            .orElseThrow(() -> new UnauthorizedException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Reset token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setFailedAttempts(0);
        user.setLockoutTime(null);
        userRepository.save(user);
        log.info("Password reset completed for user: id={}", user.getId());
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("nss_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private AuthResponse buildResponse(User user, String message) {
        return AuthResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .message(message)
            .build();
    }
}
