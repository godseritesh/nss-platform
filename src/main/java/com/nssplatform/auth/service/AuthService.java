package com.nssplatform.auth.service;

import com.nssplatform.auth.dto.AuthResponse;
import com.nssplatform.auth.dto.LoginRequest;
import com.nssplatform.auth.dto.RegisterRequest;
import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.auth.security.JwtTokenProvider;
import com.nssplatform.shared.exception.ConflictException;
import com.nssplatform.shared.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
            .name(request.getName().trim())
            .email(request.getEmail().toLowerCase().trim())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(User.Role.ROLE_USER)
            .build();
        userRepository.save(user);
        log.info("New user registered: id={}", user.getId());

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        setAuthCookie(response, token);

        return buildResponse(user, "Registration successful");
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        setAuthCookie(response, token);
        log.info("User logged in: id={}", user.getId());

        return buildResponse(user, "Login successful");
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("nss_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return buildResponse(user, null);
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("nss_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 24 hours
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
