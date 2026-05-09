package com.nssplatform.auth;

import com.nssplatform.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setup() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", "NSS-VIIT-TEST-SECRET-LONG-ENOUGH-32BYTES");
        ReflectionTestUtils.setField(provider, "jwtExpirationMs", 86400000L);
    }

    @Test
    void generateAndValidateToken() {
        String token = provider.generateToken("test@nss.in", "ROLE_USER");
        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void extractEmailFromToken() {
        String token = provider.generateToken("user@test.com", "ROLE_USER");
        assertThat(provider.getEmailFromToken(token)).isEqualTo("user@test.com");
    }

    @Test
    void extractRoleFromToken() {
        String token = provider.generateToken("admin@test.com", "ROLE_ADMIN");
        assertThat(provider.getRoleFromToken(token)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void invalidTokenReturnsFalse() {
        assertThat(provider.validateToken("not.a.real.token")).isFalse();
    }

    @Test
    void emptyTokenReturnsFalse() {
        assertThat(provider.validateToken("")).isFalse();
    }

    @Test
    void tamperedTokenReturnsFalse() {
        String token = provider.generateToken("user@test.com", "ROLE_USER");
        String tampered = token.substring(0, token.length() - 5) + "xxxxx";
        assertThat(provider.validateToken(tampered)).isFalse();
    }
}
