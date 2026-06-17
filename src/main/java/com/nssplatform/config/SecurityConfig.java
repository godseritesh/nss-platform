package com.nssplatform.config;

import com.nssplatform.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.springBoot3.config.CustomCircuitBreakerFactory;
import io.github.resilience4j.circuitbreaker.springBoot3.config.CircuitBreakerFactoryBean;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.springBoot3.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.config.CircuitBreakerConfig;

import io.github.resilience4j.cache.CaffeineCache;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.client.HttpSpanDecorator;
import org.springframework.cloud.sleuth.slf4j.SLF4JReporter;
import org.springframework.cloud.sleuth.web.DefaultHttpService;
import org.springframework.cloud.sleuth.metrics.SelfRegisteringSlf4jReporterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;
import zipkin2.reporter.okhttp3.OkHttpSpanReporter;

import zipkin2.reporter.zipkin.ZipkinReporter;

import org.springframework.cloud.sleuth.instrument.web.ClientHttpRequestsAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.web.TracedResource;
import org.springframework.cloud.sleuth.instrument.web.TraceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.Manager;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionRegistryImpl;
import org.springframework.security.web.context.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomCircuitBreakerFactory circuitBreakerFactory;

    @Value("${app.env}")
    private String appEnvironment;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(3, 65536, 1, 8, 2);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self'; connect-src 'self' https://*.tile.openstreetmap.org; frame-ancestors *"))
                .frameOptions(frame -> frame.disable())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                .contentTypeOptions(content -> {})
                .cacheControl(cache -> {})
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    CustomCircuitBreakerFactory circuitBreakerFactory() {
        return new CustomCircuitBreakerFactory();
    }

    static class CustomCircuitBreakerFactory implements CircuitBreakerFactory {
        @Override
        public CircuitBreakerConfig.Builder configureCircuitBreaker(String name) {
            return CircuitBreakerConfig.Builder.custom()
                .enableAutomaticTransition()
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .failureRateThreshold(50)
                .recordExceptionThrownInHalfOpenState(true)
                .slowCallDurationInHalfOpenState(Duration.ofMillis(2000));
        }
    }
}